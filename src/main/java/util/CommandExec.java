package util;

import org.slf4j.Logger;

import java.io.*;
import java.util.*;

public class CommandExec {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(CommandExec.class);
    final private List<List<Process>> listOfTask;

    private CommandExec(List<List<Process>> listOfTask) {
        this.listOfTask = listOfTask;
    }

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

    private void pipe(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[512];
        int read;

        while ((read = in.read(buffer, 0, buffer.length)) > -1) {
            LOG.debug("Getting new data: \n" + new String(buffer));
            out.write(buffer, 0, read);
        }
        out.close();
    }

    public static class Result {

        final private int exitCode;
        final private String output;

        private Result(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getOutput() {
            return output;
        }
    }

    public static class Builder {

        final private List<List<Process>> processes;

        public Builder() {
            processes = new LinkedList<>();
        }

        public Builder add(List<Process> newListOfProcessesInPipe) {
            processes.add(newListOfProcessesInPipe);
            return this;
        }

        public Builder addInPipe(Process... listOfProcessesInPipe) {
            final List<Process> toQueue = new LinkedList<>();
            toQueue.addAll(Arrays.asList(listOfProcessesInPipe));
            processes.add(toQueue);
            return this;
        }

        public Builder addSequentialProcess(Process... newProcesses) {
            for (final Process p : newProcesses) {
                List<Process> toQueue = new ArrayList<>();
                toQueue.add(p);
                processes.add(toQueue);
            }
            return this;
        }

        public CommandExec build() {
            return new CommandExec(processes);
        }
    }
}
