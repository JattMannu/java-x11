package x11;

import com.sun.jna.platform.unix.X11;

public enum X11Event {

    KeyPress         (2 , "xkey"             , X11.XKeyEvent.class),
    KeyRelease       (3 , "xkey"             , X11.XKeyEvent.class),
    ButtonPress      (4 , "xbutton"          , X11.XButtonEvent.class),
    ButtonRelease    (5 , "xbutton"          , X11.XButtonEvent.class),
    MotionNotify     (6 , "xmotion"          , X11.XMotionEvent.class),
    EnterNotify      (7 , "xcrossing"        , X11.XCrossingEvent.class),
    LeaveNotify      (8 , "xcrossing"        , X11.XCrossingEvent.class),
    FocusIn          (9 , "xfocus"           , X11.XFocusChangeEvent.class),
    FocusOut         (10, "xfocus"           , X11.XFocusChangeEvent.class),
    KeymapNotify     (11, "xkeymap"          , X11.XKeymapEvent.class),
    Expose           (12, "xexpose"          , X11.XExposeEvent.class),
    GraphicsExpose   (13, "xgraphicsexpose"  , X11.XGraphicsExposeEvent.class),
    NoExpose         (14, "xnoexpose"        , X11.XNoExposeEvent.class),
    VisibilityNotify (15, "xvisibility"      , X11.XVisibilityEvent.class),
    CreateNotify     (16, "xcreatewindow"    , X11.XCreateWindowEvent.class),
    DestroyNotify    (17, "xdestroywindow"   , X11.XDestroyWindowEvent.class),
    UnmapNotify      (18, "xunmap"           , X11.XUnmapEvent.class),
    MapNotify        (19, "xmap"             , X11.XMapEvent.class),
    MapRequest       (20, "xmaprequest"      , X11.XMapRequestEvent.class),
    ReparentNotify   (21, "xreparent"        , X11.XReparentEvent.class),
    ConfigureNotify  (22, "xconfigure"       , X11.XConfigureEvent.class),
    ConfigureRequest (23, "xconfigurerequest", X11.XConfigureRequestEvent.class),
    GravityNotify    (24, "xgravity"         , X11.XGravityEvent.class),
    ResizeRequest    (25, "xresizerequest"   , X11.XResizeRequestEvent.class),
    CirculateNotify  (26, "xcirculate"       , X11.XCirculateEvent.class),
    CirculateRequest (27, "xcirculaterequest", X11.XCirculateRequestEvent.class),
    PropertyNotify   (28, "xproperty"        , X11.XPropertyEvent.class),
    SelectionClear   (29, "xselectionclear"  , X11.XSelectionClearEvent.class),
    SelectionRequest (30, "xselectionrequest", X11.XSelectionRequestEvent.class),
    SelectionNotify  (31, "xselection"       , X11.XSelectionEvent.class),
    ColormapNotify   (32, "xcolormap"        , X11.XColormapEvent.class),
    ClientMessage    (33, "xclient"          , X11.XClientMessageEvent.class),
    MappingNotify    (34, "xmapping"         , X11.XMappingEvent.class);

    private final int id;
    private final String field;
    private final Class<?> eventClass;

    X11Event(int id, String field, Class<?> clazz) {
        this.id = id;
        this.field = field;
        this.eventClass = clazz;
    }

    public int getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public Class<?> getEventClass() {
        return eventClass;
    }
}
