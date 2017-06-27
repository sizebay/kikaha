package kikaha.mustache;

import kikaha.urouting.api.GET;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.Response;

import javax.inject.Singleton;

/**
 * Created by ronei.gebert on 27/06/2017.
 */
@Singleton
public class MustacheSampleResource {

    @GET
    @Path("/sample/mustache")
    public Response renderMustacheTemplate(){
        return MustacheResponse.ok()
            .templateName("/test-with-include.mustache");
    }

    @GET
    @Path("/sample/mustache-subfolder")
    public Response renderMustacheTemplateOnSubfolder(){
        return MustacheResponse.ok()
                .templateName("/another-root-path/test-with-include.mustache");
    }
}
