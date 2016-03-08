package naga.core.spi.platform.client.teavm;

import naga.core.spi.platform.client.ResourceService;
import naga.core.util.async.Future;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Bruno Salmon
 */
final class TeaVmResourceService implements ResourceService {

    public static TeaVmResourceService SINGLETON = new TeaVmResourceService();

    private TeaVmResourceService() {
    }

    @Override
    public Future<String> getText(String resourcePath) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = bufferedReader.readLine();
            while(line != null){
                sb.append(line);sb.append('\n');
                line = bufferedReader.readLine();
            }
            return Future.succeededFuture(sb.toString());
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }
}
