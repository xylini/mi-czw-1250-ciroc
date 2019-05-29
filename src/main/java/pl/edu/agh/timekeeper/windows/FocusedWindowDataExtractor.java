package pl.edu.agh.timekeeper.windows;

import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.Kernel32.*;
import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.Psapi.*;
import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.User32DLL.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;

public class FocusedWindowDataExtractor {
    private static final int MAX_LENGTH = 1024;
    private PointerByReference pointer;
    private WinDef.RECT foregroundWindowRect;

    public FocusedWindowDataExtractor(){
        this.pointer = new PointerByReference();
        this.foregroundWindowRect = new WinDef.RECT();
    }


    public String getForegroundWindowPath(){
        byte[] processPathBuffer = new byte[MAX_LENGTH];
        GetWindowThreadProcessId(GetForegroundWindow(), this.pointer);
        Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
        GetModuleFileNameExA(process, null, processPathBuffer, MAX_LENGTH);

        return Native.toString(processPathBuffer);
    }

    public WinDef.RECT getForegroundWindowRect(){
        GetWindowThreadProcessId(GetForegroundWindow(), this.pointer);
        User32.INSTANCE.GetWindowRect(GetForegroundWindow(), this.foregroundWindowRect);

        return this.foregroundWindowRect;
    }
}