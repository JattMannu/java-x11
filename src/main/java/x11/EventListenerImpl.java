package x11;

import java.io.IOException;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.XDestroyWindowEvent;
import com.sun.jna.platform.unix.X11.XErrorEvent;
import com.sun.jna.platform.unix.X11.XMapEvent;
import com.sun.jna.platform.unix.X11.XReparentEvent;

public class EventListenerImpl extends X11EventListener {

    public static void main(String[] args) throws IOException {
        EventListenerImpl listener = new EventListenerImpl();
        listener.setEventMask(X11.SubstructureNotifyMask); // just watch events
        listener.runEventLoop();
    }

    @Override
    public void onMapNotify(XMapEvent event) {
        System.out.printf("Mapped    : 0x%x\n", event.window.intValue());
        System.out.println("onMapNotify took " + time() + "ms to process");
    }

    @Override
    public void onDestroyNotify(XDestroyWindowEvent event) {
        System.out.printf("Destroyed : 0x%x\n", event.window.intValue());
        System.out.println("onDestroyNotify took " + time() + "ms to process");
    }

    @Override
    public void onReparentNotify(XReparentEvent event) {
        System.out.printf("Reparented: 0x%x to 0x%x\n", event.window.intValue(), event.parent.intValue());
        System.out.println("onReparentNotify took " + time() + "ms to process");
    }

    @Override
    int onError(Display display, XErrorEvent e) {
        byte[] buffer = new byte[1024];

        x11.XGetErrorText(display, e.error_code, buffer, buffer.length);

        System.err.printf("X Error: %s", new String(buffer));

        return 0;
    }
}
