package x11;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private Map<Integer, EventHandler> handlers = new HashMap<>();
    private List<Class<?>> handledEvents = new ArrayList<>();

    public X11EventListener() {
        x11 = X11Ext.INSTANCE;

        display = x11.XOpenDisplay(null);

        if (display == null) {
            throw new UnsupportedOperationException("cannot open display");
        }

        root = x11.XDefaultRootWindow(display);

        // register the handlers for each event type
        handlers.put(X11.KeyPress, this::_onKeyPress);
        handlers.put(X11.KeyRelease, this::_onKeyRelease);
        handlers.put(X11.ButtonPress, this::_onButtonPress);
        handlers.put(X11.ButtonRelease, this::_onButtonRelease);
        handlers.put(X11.MotionNotify, this::_onMotionNotify);
        handlers.put(X11.EnterNotify, this::_onEnterNotify);
        handlers.put(X11.LeaveNotify, this::_onLeaveNotify);
        handlers.put(X11.FocusIn, this::_onFocusIn);
        handlers.put(X11.FocusOut, this::_onFocusOut);
        handlers.put(X11.KeymapNotify, this::_onKeymapNotify);
        handlers.put(X11.Expose, this::_onExpose);
        handlers.put(X11.GraphicsExpose, this::_onGraphicsExpose);
        handlers.put(X11.NoExpose, this::_onNoExpose);
        handlers.put(X11.VisibilityNotify, this::_onVisibilityNotify);
        handlers.put(X11.CreateNotify, this::_onCreateNotify);
        handlers.put(X11.DestroyNotify, this::_onDestroyNotify);
        handlers.put(X11.UnmapNotify, this::_onUnmapNotify);
        handlers.put(X11.MapNotify, this::_onMapNotify);
        handlers.put(X11.MapRequest, this::_onMapRequest);
        handlers.put(X11.ReparentNotify, this::_onReparentNotify);
        handlers.put(X11.ConfigureNotify, this::_onConfigureNotify);
        handlers.put(X11.ConfigureRequest, this::_onConfigureRequest);
        handlers.put(X11.GravityNotify, this::_onGravityNotify);
        handlers.put(X11.ResizeRequest, this::_onResizeRequest);
        handlers.put(X11.CirculateNotify, this::_onCirculateNotify);
        handlers.put(X11.CirculateRequest, this::_onCirculateRequest);
        handlers.put(X11.PropertyNotify, this::_onPropertyNotify);
        handlers.put(X11.SelectionClear, this::_onSelectionClear);
        handlers.put(X11.SelectionRequest, this::_onSelectionRequest);
        handlers.put(X11.SelectionNotify, this::_onSelectionNotify);
        handlers.put(X11.ColormapNotify, this::_onColormapNotify);
        handlers.put(X11.ClientMessage, this::_onClientMessage);
        handlers.put(X11.MappingNotify, this::_onMappingNotify);

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

            EventHandler handler = handlers.get(e.type);

            time = System.currentTimeMillis();

            if (handler == null) {
                System.err.println("Received unknown event " + e.type);
                continue;
            }

            handler.handle(e);
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

    private void _onKeyPress(XEvent event) {
        if (handledEvents.contains(X11.XKeyEvent.class)) {
            this.onKeyPress((X11.XKeyEvent) event.readField("xkey"));
        }
    }
    private void _onKeyRelease(XEvent event) {
        if (handledEvents.contains(X11.XKeyEvent.class)) {
            this.onKeyRelease((X11.XKeyEvent) event.readField("xkey"));
        }
    }
    private void _onButtonPress(XEvent event) {
        if (handledEvents.contains(X11.XButtonEvent.class)) {
            this.onButtonPress((X11.XButtonEvent) event.readField("xbutton"));
        }
    }
    private void _onButtonRelease(XEvent event) {
        if (handledEvents.contains(X11.XButtonEvent.class)) {
            this.onButtonRelease((X11.XButtonEvent) event.readField("xbutton"));
        }
    }
    private void _onMotionNotify(XEvent event) {
        if (handledEvents.contains(X11.XMotionEvent.class)) {
            this.onMotionNotify((X11.XMotionEvent) event.readField("xmotion"));
        }
    }
    private void _onEnterNotify(XEvent event) {
        if (handledEvents.contains(X11.XCrossingEvent.class)) {
            this.onEnterNotify((X11.XCrossingEvent) event.readField("xcrossing"));
        }
    }
    private void _onLeaveNotify(XEvent event) {
        if (handledEvents.contains(X11.XCrossingEvent.class)) {
            this.onLeaveNotify((X11.XCrossingEvent) event.readField("xcrossing"));
        }
    }
    private void _onFocusIn(XEvent event) {
        if (handledEvents.contains(X11.XFocusChangeEvent.class)) {
            this.onFocusIn((X11.XFocusChangeEvent) event.readField("xfocus"));
        }
    }
    private void _onFocusOut(XEvent event) {
        if (handledEvents.contains(X11.XFocusChangeEvent.class)) {
            this.onFocusOut((X11.XFocusChangeEvent) event.readField("xfocus"));
        }
    }
    private void _onKeymapNotify(XEvent event) {
        if (handledEvents.contains(X11.XKeymapEvent.class)) {
            this.onKeymapNotify((X11.XKeymapEvent) event.readField("xkeymap"));
        }
    }
    private void _onExpose(XEvent event) {
        if (handledEvents.contains(X11.XExposeEvent.class)) {
            this.onExpose((X11.XExposeEvent) event.readField("xexpose"));
        }
    }
    private void _onGraphicsExpose(XEvent event) {
        if (handledEvents.contains(X11.XGraphicsExposeEvent.class)) {
            this.onGraphicsExpose((X11.XGraphicsExposeEvent) event.readField("xgraphicsexpose"));
        }
    }
    private void _onNoExpose(XEvent event) {
        if (handledEvents.contains(X11.XNoExposeEvent.class)) {
            this.onNoExpose((X11.XNoExposeEvent) event.readField("xnoexpose"));
        }
    }
    private void _onVisibilityNotify(XEvent event) {
        if (handledEvents.contains(X11.XVisibilityEvent.class)) {
            this.onVisibilityNotify((X11.XVisibilityEvent) event.readField("xvisibility"));
        }
    }
    private void _onCreateNotify(XEvent event) {
        if (handledEvents.contains(X11.XCreateWindowEvent.class)) {
            this.onCreateNotify((X11.XCreateWindowEvent) event.readField("xcreatewindow"));
        }
    }
    private void _onDestroyNotify(XEvent event) {
        if (handledEvents.contains(X11.XDestroyWindowEvent.class)) {
            this.onDestroyNotify((X11.XDestroyWindowEvent) event.readField("xdestroywindow"));
        }
    }
    private void _onUnmapNotify(XEvent event) {
        if (handledEvents.contains(X11.XUnmapEvent.class)) {
            this.onUnmapNotify((X11.XUnmapEvent) event.readField("xunmap"));
        }
    }
    private void _onMapNotify(XEvent event) {
        if (handledEvents.contains(X11.XMapEvent.class)) {
            this.onMapNotify((X11.XMapEvent) event.readField("xmap"));
        }
    }
    private void _onMapRequest(XEvent event) {
        if (handledEvents.contains(X11.XMapRequestEvent.class)) {
            this.onMapRequest((X11.XMapRequestEvent) event.readField("xmaprequest"));
        }
    }
    private void _onReparentNotify(XEvent event) {
        if (handledEvents.contains(X11.XReparentEvent.class)) {
            this.onReparentNotify((X11.XReparentEvent) event.readField("xreparent"));
        }
    }
    private void _onConfigureNotify(XEvent event) {
        if (handledEvents.contains(X11.XConfigureEvent.class)) {
            this.onConfigureNotify((X11.XConfigureEvent) event.readField("xconfigure"));
        }
    }
    private void _onConfigureRequest(XEvent event) {
        if (handledEvents.contains(X11.XConfigureRequestEvent.class)) {
            this.onConfigureRequest((X11.XConfigureRequestEvent) event.readField("xconfigurerequest"));
        }
    }
    private void _onGravityNotify(XEvent event) {
        if (handledEvents.contains(X11.XGravityEvent.class)) {
            this.onGravityNotify((X11.XGravityEvent) event.readField("xgravity"));
        }
    }
    private void _onResizeRequest(XEvent event) {
        if (handledEvents.contains(X11.XResizeRequestEvent.class)) {
            this.onResizeRequest((X11.XResizeRequestEvent) event.readField("xresizerequest"));
        }
    }
    private void _onCirculateNotify(XEvent event) {
        if (handledEvents.contains(X11.XCirculateEvent.class)) {
            this.onCirculateNotify((X11.XCirculateEvent) event.readField("xcirculate"));
        }
    }
    private void _onCirculateRequest(XEvent event) {
        if (handledEvents.contains(X11.XCirculateRequestEvent.class)) {
            this.onCirculateRequest((X11.XCirculateRequestEvent) event.readField("xcirculaterequest"));
        }
    }
    private void _onPropertyNotify(XEvent event) {
        if (handledEvents.contains(X11.XPropertyEvent.class)) {
            this.onPropertyNotify((X11.XPropertyEvent) event.readField("xproperty"));
        }
    }
    private void _onSelectionClear(XEvent event) {
        if (handledEvents.contains(X11.XSelectionClearEvent.class)) {
            this.onSelectionClear((X11.XSelectionClearEvent) event.readField("xselectionclear"));
        }
    }
    private void _onSelectionRequest(XEvent event) {
        if (handledEvents.contains(X11.XSelectionRequestEvent.class)) {
            this.onSelectionRequest((X11.XSelectionRequestEvent) event.readField("xselectionrequest"));
        }
    }
    private void _onSelectionNotify(XEvent event) {
        if (handledEvents.contains(X11.XSelectionEvent.class)) {
            this.onSelectionNotify((X11.XSelectionEvent) event.readField("xselection"));
        }
    }
    private void _onColormapNotify(XEvent event) {
        if (handledEvents.contains(X11.XColormapEvent.class)) {
            this.onColormapNotify((X11.XColormapEvent) event.readField("xcolormap"));
        }
    }
    private void _onClientMessage(XEvent event) {
        if (handledEvents.contains(X11.XClientMessageEvent.class)) {
            this.onClientMessage((X11.XClientMessageEvent) event.readField("xclient"));
        }
    }
    private void _onMappingNotify(XEvent event) {
        if (handledEvents.contains(X11.XMappingEvent.class)) {
            this.onMappingNotify((X11.XMappingEvent) event.readField("xmapping"));
        }
    }

    private interface EventHandler {
        void handle(XEvent e);
    }
}
