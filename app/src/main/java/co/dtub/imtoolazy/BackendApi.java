package co.dtub.imtoolazy;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

import co.dtub.imtoolazy.backend.ITLBackend;

public class BackendApi {

    public static abstract class Request<T> {

        public abstract T execute(ITLBackend api) throws IOException;
        public abstract void onResult(T result);
        public void onError(IOException e) {}
    }

    public static <T> void sendApiRequest(final Request<T> request) {

        AsyncTask<Void, Void, T> asyncTask = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... params) {
                try {
                    return request.execute(buildBackendApi());
                } catch (IOException e) {
                    request.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(T result) {
                super.onPostExecute(result);
                request.onResult(result);
            }
        };
    }

    private static ITLBackend buildBackendApi() {
        ITLBackend.Builder builder = new ITLBackend.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                null)
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        return builder.build();
    }
}
