package de.mnet.wbci.citrus;

import com.consol.citrus.context.TestContext;

/**
 * Callback for doing assertions on response objects from remote service calls. Test action implementations will invoke
 * the callback operation with service call response object.
 *
 *
 */
public interface ResponseCallback<T> {

    /**
     * Callback method invoked by test action after remote service call. Service call response object is injected as
     * method argument.
     *
     * @param responseObject
     * @param context
     */
    void doWithResponse(T responseObject, TestContext context);
}
