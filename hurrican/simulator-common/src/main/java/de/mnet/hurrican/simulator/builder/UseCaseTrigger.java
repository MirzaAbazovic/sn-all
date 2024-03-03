package de.mnet.hurrican.simulator.builder;

/**
 * Special interface marking that test builder is able to trigger a use case test with active role. This is usually the
 * case when a test builder starts to act as an interface partner with an outbound message rather than waiting for
 * inbound actions. So the simulator test builder sends the first triggering message.
 * <p/>
 * User is able to call these test builders manually through servlet user interfaces.
 *
 *
 */
public interface UseCaseTrigger {

    /**
     * Get name of trigger for display in GUI.
     *
     * @return
     */
    String getDisplayName();

    /**
     * Marks trigger as default.
     *
     * @return
     */
    boolean isDefault();
}
