package org.antifun.eclipsejardirectory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class Log {
    
    public static final String DEBUG_OPTION = "org.antifun.eclipsejardirectory.debug";
    
    public static void info(String message) {
        if ("true".equals(Platform.getDebugOption(DEBUG_OPTION))) {
            Platform.getLog(Platform.getBundle(JarDirectoryContainer.ID)).log(new Status(IStatus.INFO, JarDirectoryContainer.ID, message));
        }
    }
    
    public static void warning(String message) {
        Platform.getLog(Platform.getBundle(JarDirectoryContainer.ID)).log(new Status(IStatus.WARNING, JarDirectoryContainer.ID, message));
    }
    
    public static void error(String message) {
        Platform.getLog(Platform.getBundle(JarDirectoryContainer.ID)).log(new Status(IStatus.ERROR, JarDirectoryContainer.ID, message));
    }
    
}
