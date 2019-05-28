package pl.edu.agh.timekeeper.windows;

import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.Kernel32.*;
import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.Psapi.*;
import static pl.edu.agh.timekeeper.windows.WindowsNativeAPI.User32DLL.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import pl.edu.agh.timekeeper.timer.Timer;

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
<<<<<<< HEAD

        GetWindowThreadProcessId(GetForegroundWindow(), this.pointer);
        Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
        GetModuleFileNameExA(process, null, processPathBuffer, MAX_LENGTH);

        return Native.toString(processPathBuffer);
=======
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
        }
>>>>>>> ae81a0718b11816ab69f8931d7d4db0a4060aeeb
    }

    public WinDef.RECT getForegroundWindowRect(){
        GetWindowThreadProcessId(GetForegroundWindow(), this.pointer);
        User32.INSTANCE.GetWindowRect(GetForegroundWindow(), this.foregroundWindowRect);

        return foregroundWindowRect;
    }
//    public void run() {
//        byte[] processPathBuffer = new byte[MAX_LENGTH];
//        PointerByReference pointer = new PointerByReference();
//        Timer timer = Timer.getInstance();
//        WinDef.RECT foregroundWindowRect = new WinDef.RECT();
//        boolean isViewerStarted = false;
//
//        while (true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            GetWindowThreadProcessId(GetForegroundWindow(), pointer);
//            User32.INSTANCE.GetWindowRect(GetForegroundWindow(), foregroundWindowRect);
//            if(!isViewerStarted){
//                isViewerStarted = true;
//                timer.StartTimerView(foregroundWindowRect);
//            }
//            Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
//            GetModuleFileNameExA(process, null, processPathBuffer, MAX_LENGTH);
//            String focusedWindowProcessPath = Native.toString(processPathBuffer);
//            timer.setApplicationPath(focusedWindowProcessPath, foregroundWindowRect);
//
//            System.out.print(focusedWindowProcessPath + "    ");
//            System.out.println(timer.getCurrentProgramSeconds());
//            System.out.println(timer.getCurrentWindowUsageTime());
//        }
//    }
}