package pl.edu.agh.timekeeper.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.PointerByReference;

public class WindowsNativeAPI {
    static class Psapi {
        static {
            Native.register("psapi");
        }

        public static native int GetModuleBaseNameW(Pointer process, Pointer module, char[] lpBaseName, int size);

        public static native int GetModuleFileNameExA(Pointer process, Pointer module, byte[] name, int i);
    }

    static class Kernel32 {
        static {
            Native.register("kernel32");
        }

        public static int PROCESS_QUERY_INFORMATION = 0x0400;
        public static int PROCESS_VM_READ = 0x0010;

        public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
    }

    static class User32DLL {
        static {
            Native.register("user32");
        }

        public static native int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);

        public static native WinDef.HWND GetForegroundWindow();

        public static native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);

        public static native boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);

        public static native boolean SetForegroundWindow(WinDef.HWND hWnd);
    }
}
