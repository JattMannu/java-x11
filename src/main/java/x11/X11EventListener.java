package x11;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.Window;
import com.sun.jna.platform.unix.X11.XErrorEvent;
import com.sun.jna.platform.unix.X11.XEvent;

public abstract class X11EventListener {

    /**
     * SubstructureNotifyMask | SubstructureRedirectMask
     */
    static final int WM_EVENT_MASK = X11.SubstructureNotifyMask | X11.SubstructureRedirectMask;

    final X11Ext x11;
    final Display display;
    final Window root;

    private int eventMask = WM_EVENT_MASK;
    private boolean running = false;
    private boolean debug = false;

    private List<Class<?>> handledEvents = new ArrayList<>();

    public X11EventListener() {
        x11 = X11Ext.INSTANCE;

        display = x11.XOpenDisplay(null);

        if (display == null) {
            throw new UnsupportedOperationException("cannot open display");
        }

        root = x11.XDefaultRootWindow(display);

        // register the handlers for each event type
        registerListener(X11Event.KeyPress, this::onKeyPress);
        registerListener(X11Event.KeyRelease, this::onKeyRelease);
        registerListener(X11Event.ButtonPress, this::onButtonPress);
        registerListener(X11Event.ButtonRelease, this::onButtonRelease);
        registerListener(X11Event.MotionNotify, this::onMotionNotify);
        registerListener(X11Event.EnterNotify, this::onEnterNotify);
        registerListener(X11Event.LeaveNotify, this::onLeaveNotify);
        registerListener(X11Event.FocusIn, this::onFocusIn);
        registerListener(X11Event.FocusOut, this::onFocusOut);
        registerListener(X11Event.KeymapNotify, this::onKeymapNotify);
        registerListener(X11Event.Expose, this::onExpose);
        registerListener(X11Event.GraphicsExpose, this::onGraphicsExpose);
        registerListener(X11Event.NoExpose, this::onNoExpose);
        registerListener(X11Event.VisibilityNotify, this::onVisibilityNotify);
        registerListener(X11Event.CreateNotify, this::onCreateNotify);
        registerListener(X11Event.DestroyNotify, this::onDestroyNotify);
        registerListener(X11Event.UnmapNotify, this::onUnmapNotify);
        registerListener(X11Event.MapNotify, this::onMapNotify);
        registerListener(X11Event.MapRequest, this::onMapRequest);
        registerListener(X11Event.ReparentNotify, this::onReparentNotify);
        registerListener(X11Event.ConfigureNotify, this::onConfigureNotify);
        registerListener(X11Event.ConfigureRequest, this::onConfigureRequest);
        registerListener(X11Event.GravityNotify, this::onGravityNotify);
        registerListener(X11Event.ResizeRequest, this::onResizeRequest);
        registerListener(X11Event.CirculateNotify, this::onCirculateNotify);
        registerListener(X11Event.CirculateRequest, this::onCirculateRequest);
        registerListener(X11Event.PropertyNotify, this::onPropertyNotify);
        registerListener(X11Event.SelectionClear, this::onSelectionClear);
        registerListener(X11Event.SelectionRequest, this::onSelectionRequest);
        registerListener(X11Event.SelectionNotify, this::onSelectionNotify);
        registerListener(X11Event.ColormapNotify, this::onColormapNotify);
        registerListener(X11Event.ClientMessage, this::onClientMessage);
        registerListener(X11Event.MappingNotify, this::onMappingNotify);

        /*
         * Gets all the overridden methods by the child class,
         * used to prevent reading data from unhandled events.
         */
        for (Method m : getClass().getMethods()) {
            if (m.getDeclaringClass() == getClass()) {
                Class<?>[] pTypes = m.getParameterTypes();
                if (pTypes.length == 1 && m.getName().startsWith("on") && m.getReturnType() == void.class) {
                    String pZero = pTypes[0].getCanonicalName();
                    if (pZero.startsWith("com.sun.jna.platform.unix.X11.X") && pZero.endsWith("Event")) {
                        handledEvents.add(pTypes[0]);
                        System.out.println("Added handler: " + m.getName());
                    }
                }
            }
        }
    }

    public void onKeyPress(X11.XKeyEvent event) {}
    public void onKeyRelease(X11.XKeyEvent event) {}
    public void onButtonPress(X11.XButtonEvent event) {}
    public void onButtonRelease(X11.XButtonEvent event) {}
    public void onMotionNotify(X11.XMotionEvent event) {}
    public void onEnterNotify(X11.XCrossingEvent event) {}
    public void onLeaveNotify(X11.XCrossingEvent event) {}
    public void onFocusIn(X11.XFocusChangeEvent event) {}
    public void onFocusOut(X11.XFocusChangeEvent event) {}
    public void onKeymapNotify(X11.XKeymapEvent event) {}
    public void onExpose(X11.XExposeEvent event) {}
    public void onGraphicsExpose(X11.XGraphicsExposeEvent event) {}
    public void onNoExpose(X11.XNoExposeEvent event) {}
    public void onVisibilityNotify(X11.XVisibilityEvent event) {}
    public void onCreateNotify(X11.XCreateWindowEvent event) {}
    public void onDestroyNotify(X11.XDestroyWindowEvent event) {}
    public void onUnmapNotify(X11.XUnmapEvent event) {}
    public void onMapNotify(X11.XMapEvent event) {}
    public void onMapRequest(X11.XMapRequestEvent event) {}
    public void onReparentNotify(X11.XReparentEvent event) {}
    public void onConfigureNotify(X11.XConfigureEvent event) {}
    public void onConfigureRequest(X11.XConfigureRequestEvent event) {}
    public void onGravityNotify(X11.XGravityEvent event) {}
    public void onResizeRequest(X11.XResizeRequestEvent event) {}
    public void onCirculateNotify(X11.XCirculateEvent event) {}
    public void onCirculateRequest(X11.XCirculateRequestEvent event) {}
    public void onPropertyNotify(X11.XPropertyEvent event) {}
    public void onSelectionClear(X11.XSelectionClearEvent event) {}
    public void onSelectionRequest(X11.XSelectionRequestEvent event) {}
    public void onSelectionNotify(X11.XSelectionEvent event) {}
    public void onColormapNotify(X11.XColormapEvent event) {}
    public void onClientMessage(X11.XClientMessageEvent event) {}
    public void onMappingNotify(X11.XMappingEvent event) {}

    abstract int onError(Display display, XErrorEvent e);

    private long time = 0;

    /**
     * Get the time in milliseconds since the last event was called
     */
    long time() { return System.currentTimeMillis() - time; }

    final boolean runEventLoop() {
        if (running) throw new IllegalStateException("already running!");

        AtomicBoolean wm_detected = new AtomicBoolean(false);

        x11.XSetErrorHandler((d, e) -> {
            wm_detected.set(true);
            return 0;
        });

        x11.XSelectInput(display, root, new NativeLong(eventMask));
        x11.XSync(display, false);

        if (wm_detected.get()) {
            System.err.println("Another Window Manager is running!");
            return false;
        }

        running = true;

        x11.XSetErrorHandler(this::onError);

        preLoop();

        while (running) {
            final XEvent e = new XEvent();
            x11.XNextEvent(display, e);

            time = System.currentTimeMillis();

            callEvent(e.type, e);
        }

        return true;
    }

    public int getEventMask() {
        return eventMask;
    }

    /**
     * Set the event mask to use in XSelectInput
     * @param eventMask the mask to use
     */
    public void setEventMask(int eventMask) {
        this.eventMask = eventMask;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Run something before the event loop starts
     */
    void preLoop() {}

    /**
     * Stop the event loop
     */
    final void stopEventLoop() {
        running = false;
    }

    /**
     * Check if the event loop is running
     */
    public final boolean isRunning() {
        return running;
    }

    /*==================================================================*/
    /* Internal methods below */
    /*==================================================================*/

    private Map<Integer, Consumer<XEvent>> handlers = new HashMap<>();

    private <T> void registerListener(X11Event eventType, EventHandler<T> handler) {
        handlers.put(eventType.getId(), event -> {
            if (handledEvents.contains(eventType.getEventClass())) {
                Object o = event.readField(eventType.getField());
                handler.handle0(o);
            }
        });
    }

    private void callEvent(int eventId, XEvent event) {
        handlers.get(eventId).accept(event);
    }

    private interface EventHandler<T> {
        void handle(T event);

        @SuppressWarnings("unchecked")
        default void handle0(Object o) {
            handle((T) o);
        }
    }
}
