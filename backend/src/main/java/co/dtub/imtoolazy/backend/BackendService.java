package co.dtub.imtoolazy.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(
        name = "backend",
        canonicalName = "ITL Backend",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "imtoolazy.dtub.co",
                ownerName = "DevTub"))
public class BackendService {
}
