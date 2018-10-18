package org.augugrumi.harbor.routes.nfvo.nsm;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.augugrumi.harbor.k8s.K8sAPI;
import org.augugrumi.harbor.k8s.K8sRetriever;
import org.augugrumi.harbor.persistence.Result;
import org.augugrumi.harbor.persistence.data.DataWizard;
import org.augugrumi.harbor.persistence.data.NetworkService;
import org.augugrumi.harbor.persistence.data.VirtualNetworkFunction;
import org.augugrumi.harbor.routes.util.Errors;
import org.augugrumi.harbor.routes.util.ParamConstants;
import org.augugrumi.harbor.routes.util.jsonstructure.Roulette;
import org.augugrumi.harbor.util.ConfigManager;
import org.augugrumi.harbor.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.InetAddress;

public class NsLauncherRoute implements Route {

    private final static Logger LOG = ConfigManager.getConfig().getApplicationLogger(NsLauncherRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.debug(this.getClass().getSimpleName() + " called");
        final Result<NetworkService> nsRes = DataWizard.getNs(request.params(ParamConstants.ID));
        if (nsRes.isSuccessful()) {
            final NetworkService ns = nsRes.getContent();
            for (final VirtualNetworkFunction vnf : ns.getChain()) {
                final String definition = vnf.getDefinition();
                final K8sAPI k8s = K8sRetriever.getK8sAPI();
                k8s.createFromYaml(FileUtils.createTmpFile("hrbr", ".yaml",
                        vnf.getDefinition()).toURI().toURL(),
                        res -> {
                            LOG.info(res.getAttachment().toString());
                            return res.getAttachment().toString();
                        }); // TODO should check if the deployment it's ok
            }
            final int spi = ns.getSPI();
            final InetAddress[] roulette = InetAddress.getAllByName(ConfigManager.getConfig().getRouletteUrl().getHost());
            for (final InetAddress r : roulette) {
                // Make request to update the entry in the roulette DB
                final MediaType json = MediaType.parse("application/json; charset=utf-8");
                final JSONObject update = new JSONObject();
                final JSONArray si = new JSONArray();
                ns.getChain().forEach(item -> {
                    final JSONObject singleSI = new JSONObject();
                    singleSI.put(Roulette.SI.URL, item.getID());
                    singleSI.put(Roulette.SI.PORT, 80); // FIXME find out the port kubernetes gave to the service!
                    si.put(singleSI);
                });
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(json, update.toString());
                okhttp3.Request postRequest = new okhttp3.Request.Builder()
                        .url(r.toString() + "/routes/" + spi)
                        .post(body)
                        .build();
                okhttp3.Response postResponse = client.newCall(postRequest).execute();

                if (postResponse.isSuccessful()) {
                    ResponseBody bodyResponse = postResponse.body();
                    if (bodyResponse != null) {
                        JSONObject jsonResponse = new JSONObject(bodyResponse.string());
                        if (!ResponseCreator.ResponseType.OK.toString().equalsIgnoreCase(
                                jsonResponse.optString(ResponseCreator.Fields.RESULT.toString().toLowerCase(),
                                        ""))) {
                            return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                                    .add(ResponseCreator.Fields.REASON, Errors.ROULETTE_UPDATE_FAILURE);
                        }
                    } else {
                        return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                                .add(ResponseCreator.Fields.REASON, Errors.ROULETTE_EMPTY_REPLY);
                    }
                } else {
                    return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                            .add(ResponseCreator.Fields.REASON, Errors.ROULETTE_UPDATE_FAILURE);
                }
            }
            // If the codes arrives here, it means there hasn't been errors, so we return an ok json reply
            return new ResponseCreator(ResponseCreator.ResponseType.OK);
        }
        return new ResponseCreator(ResponseCreator.ResponseType.ERROR)
                .add(ResponseCreator.Fields.REASON, Errors.NO_SUCH_ELEMENT);
    }
}