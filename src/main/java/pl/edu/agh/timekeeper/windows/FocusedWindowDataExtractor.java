package pl.edu.agh.timekeeper.windows;

import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.Kernel32.*;
import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.Psapi.*;
import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.User32DLL.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import pl.edu.agh.timekeeper.timer.Timer;

public class FocusedWindowDataExtractor implements Runnable {
    private static final int MAX_LENGTH = 1024;

    public void run() {
        byte[] processPathBuffer = new byte[MAX_LENGTH];
        PointerByReference pointer = new PointerByReference();
        Timer timer = Timer.getInstance();
        timer.setApplicationPath("");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetWindowThreadProcessId(GetForegroundWindow(), pointer);
            Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
            GetModuleFileNameExA(process, null, processPathBuffer, MAX_LENGTH);
            String focusedWindowProcessPath = Native.toString(processPathBuffer);
            timer.setApplicationPath(focusedWindowProcessPath);
            System.out.print(focusedWindowProcessPath + "    ");
            System.out.println(timer.getSecondsUsedToday());
        }
    }


}