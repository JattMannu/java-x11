package x11;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.unix.X11;

public interface X11Ext extends X11 {

    X11Ext INSTANCE = Native.load("X11", X11Ext.class);

    int XGrabServer(Display display);
    int XUngrabServer(Display display);

    int XConfigureWindow(Display display, Window w, NativeLong value_mask, XWindowChanges changes);
    int XMoveWindow(Display display, Window w, int x, int y);
    int XResizeWindow(Display display, Window w, int width, int height);
    int XMoveResizeWindow(Display display, Window w, int x, int y, int width, int height);
    int XSetWindowBorderWidth(Display display, Window w, int width);

    int XRaiseWindow(Display display, Window w);

    int XChangeSaveSet(Display display, Window w, int change_mode);
    int XAddToSaveSet(Display display, Window w);
    int XRemoveFromSaveSet(Display display, Window w);

    int XReparentWindow(Display display, Window w, Window parent, int x, int y);

    int XGrabButton(Display display, int button, int modifiers, Window grab_window, boolean owner_events, int event_mask, int pointer_mode, int keyboard_mode, Window confine_to, Cursor cursor);

    int XKillClient(Display display, XID resource);
    int XSetInputFocus(Display display, Window focus, int revert_to, int time);

    int XSetClassHint(Display display, Window w, XClassHint class_hints);

    int SetModeInsert = 0;
    int SetModeDelete = 1;

    int XK_Tab = 0xFF09;

    int XK_F1 = 0xFFBE;
    int XK_F2 = 0xFFBF;
    int XK_F3 = 0xFFC0;
    int XK_F4 = 0xFFC1;
    int XK_F5 = 0xFFC2;
    int XK_F6 = 0xFFC3;
    int XK_F7 = 0xFFC4;
    int XK_F8 = 0xFFC5;
    int XK_F9 = 0xFFC6;
    int XK_F10 = 0xFFC7;
    int XK_F11 = 0xFFC8;

    int XK_1 = 0x031;
    int XK_2 = 0x032;
    int XK_3 = 0x033;
    int XK_4 = 0x034;
    int XK_5 = 0x035;
    int XK_6 = 0x036;
    int XK_7 = 0x037;
    int XK_8 = 0x038;
    int XK_9 = 0x039;

    @FieldOrder({ "res_name", "res_class" })
    class XClassHint extends Structure {
        public String res_name;
        public String res_class;
        public XClassHint(String res_name, String res_class) {
            this.res_name = res_name;
            this.res_class = res_class;
        }
    }

    @FieldOrder({ "x", "y", "width", "height", "border_width", "sibling", "stack_mode" })
    class XWindowChanges extends Structure {
        public int x, y;
        public int width, height;
        public int border_width;
        public Window sibling;
        public int stack_mode;
    }
}
