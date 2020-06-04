package se.leafcoders.rosette.endpoint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.leafcoders.rosette.test.matcher.Matchers.*;

import org.springframework.test.web.servlet.ResultActions;

import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.endpoint.user.User;

public class CommonRequestTests {

    private final AbstractControllerTest act;
    private final String modelName;

    public <T extends Persistable> CommonRequestTests(AbstractControllerTest act, Class<T> modelClass) {
        this.act = act;
        this.modelName = modelClass.getSimpleName();
    }

    // GET ONE -------------------

    public ResultActions getOneExpectForbidden(User authUser, String controllerUrl, Long id) throws Exception {
        return act.withUser(authUser, get(controllerUrl + "/" + id))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.FORBIDDEN)))
                .andExpect(jsonPath("$.reason", isApiError(ApiError.MISSING_PERMISSION)));
    }

    public ResultActions getOneExpectNotFound(User authUser, String controllerUrl, Long id) throws Exception {
        return act.withUser(authUser, get(controllerUrl + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.NOT_FOUND)))
                .andExpect(jsonPath("$.reason",
                        is("Id (" + id + ") of resource type (" + modelName
                                + ") was not found.")));
    }

    public ResultActions getOneSuccess(User authUser, String controllerUrl, Long id) throws Exception {
        return act.withUserPrint(authUser, get(controllerUrl + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.id", is(id.intValue())));
    }

    public ResultActions getSuccess(User authUser, String url) throws Exception {
        return act.withUserPrint(authUser, get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON));
    }

    public ResultActions allGetOneTests(User authUser, String permission, String controllerUrl, Long id)
            throws Exception {
        // No permission
        getOneExpectForbidden(authUser, controllerUrl, id);

        act.givenPermissionForUser(authUser, permission);

        // With permission but invalid id
        getOneExpectNotFound(authUser, controllerUrl, 1234567L);

        // With permission
        return getOneSuccess(authUser, controllerUrl, id);
    }

    // GET MANY ------------------

    public ResultActions getManySuccess(User authUser, String controllerUrl) throws Exception {
        return act.withUser(authUser, get(controllerUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON));
    }

    public ResultActions getManySuccessRssXml(User authUser, String controllerUrl) throws Exception {
        return act.withUser(authUser, get(controllerUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_RSSXML));
    }

    public ResultActions getManyExpectForbidden(User authUser, String controllerUrl) throws Exception {
        return act.withUser(authUser, get(controllerUrl))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.FORBIDDEN)))
                .andExpect(jsonPath("$.reason", isApiError(ApiError.MISSING_PERMISSION)));
    }

    public ResultActions allGetManyTests(User authUser, String permission, String controllerUrl) throws Exception {
        // No permission
        getManySuccess(authUser, controllerUrl)
                .andExpect(jsonPath("$", hasSize(0)));

        act.givenPermissionForUser(authUser, permission);

        // With permission
        return getManySuccess(authUser, controllerUrl);
    }

    // POST ----------------------

    public ResultActions postExpectMissingPermission(User authUser, String controllerUrl, String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        post(controllerUrl).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.FORBIDDEN)))
                .andExpect(jsonPath("$.reason", isApiError(ApiError.MISSING_PERMISSION)));
    }

    public ResultActions postExpectForbidden(ApiError reason, User authUser, String controllerUrl,
            String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        post(controllerUrl).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.FORBIDDEN)))
                .andExpect(jsonPath("$.reason", isApiError(reason)));
    }

    public ResultActions postExpectBadRequest(User authUser, String controllerUrl, String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        post(controllerUrl).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON));
    }

    public ResultActions postSuccess(User authUser, String controllerUrl, String jsonString) throws Exception {
        return act
                .withUser(authUser,
                        post(controllerUrl).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.id", anything()));
    }

    public ResultActions postSuccessWithOk(User authUser, String controllerUrl, String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        post(controllerUrl).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON));
    }

    public ResultActions allPostTests(User authUser, String permission, String controllerUrl, String jsonString)
            throws Exception {
        // No permission
        postExpectMissingPermission(authUser, controllerUrl, jsonString);

        act.givenPermissionForUser(authUser, permission);

        // With permission
        return postSuccess(authUser, controllerUrl, jsonString);
    }

    // PUT ----------------------

    public ResultActions putExpectNotFound(User authUser, String controllerUrl, Long id, String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        put(controllerUrl + "/" + id).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.NOT_FOUND)))
                .andExpect(jsonPath("$.reason",
                        is("Id (" + id + ") of resource type (" + modelName
                                + ") was not found.")));
    }

    public ResultActions putExpectForbidden(User authUser, String controllerUrl, Long id, String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        put(controllerUrl + "/" + id).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.FORBIDDEN)))
                .andExpect(jsonPath("$.reason", isApiError(ApiError.MISSING_PERMISSION)));
    }

    public ResultActions putSuccess(User authUser, String controllerUrl, Long id, String jsonString)
            throws Exception {
        return act
                .withUser(authUser,
                        put(controllerUrl + "/" + id).content(jsonString)
                                .contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.id", anything()));
    }

    public ResultActions allPutTests(User authUser, String permission, String controllerUrl, Long id,
            String jsonString)
            throws Exception {
        // No permission
        putExpectForbidden(authUser, controllerUrl, id, jsonString);

        act.givenPermissionForUser(authUser, permission);

        // With permission but invalid id
        putExpectNotFound(authUser, controllerUrl, 1234567L, jsonString);

        // With permission
        return putSuccess(authUser, controllerUrl, id, jsonString);
    }

    // DELETE --------------------

    public ResultActions deleteExpectNotFound(User authUser, String controllerUrl, Long id) throws Exception {
        return act.withUser(authUser, delete(controllerUrl + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.NOT_FOUND)))
                .andExpect(jsonPath("$.reason",
                        is("Id (" + id + ") of resource type (" + modelName
                                + ") was not found.")));
    }

    public ResultActions deleteExpectForbidden(User authUser, String controllerUrl, Long id) throws Exception {
        return act.withUser(authUser, delete(controllerUrl + "/" + id))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
                .andExpect(jsonPath("$.error", isApiError(ApiError.FORBIDDEN)))
                .andExpect(jsonPath("$.reason", isApiError(ApiError.MISSING_PERMISSION)));
    }

    public ResultActions deleteSuccess(User authUser, String controllerUrl, Long id) throws Exception {
        return act.withUser(authUser, delete(controllerUrl + "/" + id))
                .andExpect(status().isNoContent());
    }

    public void allDeleteTests(User authUser, String permission, String controllerUrl, Long id) throws Exception {
        // No permission
        deleteExpectForbidden(authUser, controllerUrl, id);

        // With permission
        act.givenPermissionForUser(authUser, permission);
        deleteSuccess(authUser, controllerUrl, id);

        // Not found because it's deleted
        deleteExpectNotFound(authUser, controllerUrl, id);
    }

    // ADD CHILD -----------------

    public ResultActions allAddChildTests(User authUser, String permission, String controllerUrl, Long childId,
            String jsonString) throws Exception {
        // No permission
        postExpectMissingPermission(authUser, controllerUrl + (childId != null ? "/" + childId : ""),
                jsonString);

        act.givenPermissionForUser(authUser, permission);

        // With permission
        return postSuccessWithOk(authUser, controllerUrl + (childId != null ? "/" + childId : ""), jsonString);
    }

    // GET CHILDREN ------------

    public ResultActions allGetChildrenTests(User authUser, String permission, String controllerUrl)
            throws Exception {
        // No permission
        getManyExpectForbidden(authUser, controllerUrl);

        act.givenPermissionForUser(authUser, permission);

        // With permission
        return getManySuccess(authUser, controllerUrl);
    }

    // REMOVE CHILD ---------------

    public ResultActions allRemoveChildTests(User authUser, String permission, String controllerUrl, Long childId)
            throws Exception {
        // No permission
        deleteExpectForbidden(authUser, controllerUrl, childId);

        act.givenPermissionForUser(authUser, permission);

        // With permission
        return act.withUser(authUser, delete(controllerUrl + "/" + childId))
                .andExpect(status().isOk());
    }

}
