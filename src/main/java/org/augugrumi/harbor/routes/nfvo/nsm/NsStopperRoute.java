package org.augugrumi.harbor.routes.nfvo.nsm;

import okhttp3.OkHttpClient;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.persistence.data.NetworkService;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.routes.util.InetAddressFilter;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.routes.util.UpdateError;
import org.augugrumi.harbor.util.ConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class NsStopperRoute implements Route {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(NsStopperRoute.class);

    @Override
    public Object handle(Request request, Response response) throws IOException {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Result<NetworkService> nsRes = DataWizard.getNs(request.params(ParamConstants.ID));
        if (!nsRes.isSuccessful()) {
            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, Errors.NO_SUCH_ELEMENT);
        }
        final NetworkService ns = nsRes.getContent();
        final int spi = ns.getSPI();
        final URL rouletteUrl = ConfigManager.getConfig().getRouletteUrl();

        final List<InetAddress> roulette = InetAddressFilter.filterIPv6(rouletteUrl);
        final List<UpdateError> erroneousRouletteUpdate = new LinkedList<>();
        for (final InetAddress r : roulette) {

            OkHttpClient client = new OkHttpClient();
            int roulettePort = rouletteUrl.getPort() == -1 ? rouletteUrl.getDefaultPort() : rouletteUrl.getPort();
            final String url = rouletteUrl.getProtocol() + "://" + r.getHostAddress() + ":" + roulettePort +
                    "/routes/" + spi;
            LOG.debug(spi + " - Url to send: " + url);

            okhttp3.Request deleteRequest = new okhttp3.Request.Builder()
                    .url(url)
                    .delete()
                    .build();
            okhttp3.Response deleteResponse = client.newCall(deleteRequest).execute();

            final List<UpdateError> sessionError = new LinkedList<>();
            if (!deleteResponse.isSuccessful() || deleteResponse.code() != 200) {
                LOG.error("The Roulette post response was not successful for entry " + spi + ". Aborting route update");
                sessionError.add(new UpdateError(r, "The Roulette post response was not successful for entry " +
                        spi + ". Aborting route update"));
            } else if (deleteResponse.body() == null) {
                sessionError.add(new UpdateError(r, Errors.ROULETTE_EMPTY_REPLY));
            } else {
                JSONObject jsonResponse = new JSONObject(deleteResponse.body().string());
                if (!ResponseCreator.ResponseType.OK.toString().equalsIgnoreCase(
                        jsonResponse.optString(ResponseCreator.Fields.RESULT.toString().toLowerCase(),
                                ""))) {
                    sessionError.add(new UpdateError(r, Errors.ROULETTE_UPDATE_FAILURE));
                }
            }
            erroneousRouletteUpdate.addAll(sessionError);
            deleteResponse.close();
        }

        ResponseCreator responseCreator;

        if (erroneousRouletteUpdate.size() > 0) {
            // Errors happened during the roulette update
            final StringBuilder errors = new StringBuilder();
            for (final UpdateError ue : erroneousRouletteUpdate) {
                errors.append(ue.toString())
                        .append('\n');
            }
            final String reason = Errors.ROULETTE_UPDATE_NOT_COMPLETE + '\n' + errors.toString();
            responseCreator = new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                    .add(ResponseCreator.Fields.REASON, reason);
        } else {
            responseCreator = new ResponseCreator(ResponseCreator.ResponseType.OK);
        }
        ns.setStatus(NetworkService.STATUS_DOWN);

        return responseCreator;
    }
}
