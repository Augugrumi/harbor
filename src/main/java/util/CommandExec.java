package util;

import org.slf4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Command line executor. It allows to pipe commands between then or to stack different commands to be executed in
 * batch. On top of that, it stores the stdin output for each command, so that is possible to retrieve the command
 * output.
 */
public class CommandExec {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(CommandExec.class);
    final private List<List<Process>> listOfTask;

    /**
     * Initialize the command executor
     *
     * @param listOfTask a list of list of processes to exec
     */
    private CommandExec(List<List<Process>> listOfTask) {
        this.listOfTask = listOfTask;
    }

    /**
     * Process the list of commands given by the Builder
     * @return a map containing for each list of processes a String containing the process output
     * @see Builder
     */
    public Map<List<Process>, Result> exec() {

        final Map<List<Process>, Result> outputResult = new HashMap<>();

        // Sequential task
        listOfTask.forEach(processInPipe -> {
            for (int i = 0; (i + 1) < processInPipe.size(); i++) {

                Process p1 = processInPipe.get(i);
                Process p2 = processInPipe.get(i + 1);
                LOG.trace("Executing task n: " + i);

                try {
                    if (p2.isAlive()) {
                        pipe(p1.getInputStream(), p2.getOutputStream());
                        p2.waitFor();
                    } else {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    LOG.error("Failed to pipe processes: " + p1.toString() + " and " + p2.toString());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Process last = processInPipe.get(processInPipe.size() - 1);
            try {
                LOG.trace("Waiting for last task to finish...");
                last.waitFor();
                LOG.trace("Done");
            } catch (InterruptedException e) {
                LOG.error("Last process didn't finish in time");
                e.printStackTrace();
            }

            try {
                outputResult.put(processInPipe, getOutputFromCommand(last));
            } catch (IOException e) {
                LOG.error("Impossible to get output from last command");
                e.printStackTrace();
            }

        });

        return outputResult;
    }

    /**
     * It takes a process and it obtains stdin if the program exited with status 0, otherwise it gets stderr
     * @param prc the process from which the output is taken
     * @return A result composed by the process exit value and the output log.
     * @throws IOException exception when reading the InputStream
     * @see Result
     */
    private Result getOutputFromCommand(Process prc) throws IOException {

        InputStream is;

        if (prc.exitValue() != 0) {
            is = prc.getErrorStream();
        } else {
            is = prc.getInputStream();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder res = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            res.append(line);
        }
        return new Result(prc.exitValue(), res.toString());
    }

    /**
     * It redirects the output of a command as an input of the next one
     * @param in the input stream of the first command
     * @param out the output stream of the second command
     * @throws IOException is launched if there are problems reading one of the two streams
     */
    private void pipe(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[512];
        int read;

        while ((read = in.read(buffer, 0, buffer.length)) > -1) {
            LOG.debug("Getting new data: \n" + new String(buffer));
            out.write(buffer, 0, read);
        }
        out.close();
    }

    /**
     * This POJO contains the exit code of the process and the log of the process itself
     * @see <a href="https://en.wikipedia.org/wiki/Plain_old_Java_object">POJO</a>
     */
    public static class Result {

        final private int exitCode;
        final private String output;

        /**
         * Internal constructor
         * @param exitCode the exit code of the process
         * @param output the log of the process
         */
        private Result(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        /**
         * Getter method to obtain the process exit code
         * @return the process exit code
         */
        public int getExitCode() {
            return exitCode;
        }

        /**
         * Getter method to obtain the process log
         * @return the process log
         */
        public String getOutput() {
            return output;
        }
    }

    /**
     * CommandExec builder class. It builds a valid command executor, giving the possibility to add different processes.
     * Every process needs to be inside a list. A list is seen as a chain of commands to be piped together. In this way,
     * a list with a single process is just a single command to exec, while a list with multiple processes is a command
     * that contains process to pipe together. The piping works in this way: the head of the list is the fist command
     * to be executed, like in a stack (FIFO), and the output of the first command is passed as input of the second one.
     *
     * The exit value of the command is the result of the logical "and" between all the exit values of every single
     * process in the list.
     * @see CommandExec
     * @see List
     * @see Process
     * @see <a href="https://en.wikipedia.org/wiki/FIFO_(computing_and_electronics)">FIFO</a>
     */
    public static class Builder {

        final private List<List<Process>> processes;

        /**
         * It creates an empty list of process
         */
        public Builder() {
            processes = new LinkedList<>();
        }

        /**
         * It adds a single command to the commands queue
         * @param newListOfProcessesInPipe a new command to exec
         * @return the builder itself
         */
        public Builder add(List<Process> newListOfProcessesInPipe) {
            processes.add(newListOfProcessesInPipe);
            return this;
        }

        /**
         * It adds a single command to the commands queue
         * @param listOfProcessesInPipe a new command to exec
         * @return the builder itself
         */
        public Builder addInPipe(Process... listOfProcessesInPipe) {
            final List<Process> toQueue = new LinkedList<>();
            toQueue.addAll(Arrays.asList(listOfProcessesInPipe));
            processes.add(toQueue);
            return this;
        }

        /**
         * For every process, it adds a new command to the commands queue. This is useful to bulk add new commands
         * @param newProcesses the new commands to queue
         * @return the builder itself
         */
        public Builder addSequentialProcess(Process... newProcesses) {
            for (final Process p : newProcesses) {
                List<Process> toQueue = new ArrayList<>();
                toQueue.add(p);
                processes.add(toQueue);
            }
            return this;
        }

        /**
         * Builds the CommandExec object
         * @return a CommandExec object
         * @see CommandExec
         */
        public CommandExec build() {
            return new CommandExec(processes);
        }
    }
}
