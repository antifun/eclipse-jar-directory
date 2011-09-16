package org.antifun.eclipsejardirectory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JarDirectoryContainerInitializer extends ClasspathContainerInitializer {

    @Override
    public void initialize(IPath path, IJavaProject project) throws CoreException {
        JarDirectoryContainer container = new JarDirectoryContainer(path, project);
        if (container.isValid()) {
            JavaCore.setClasspathContainer(path, new IJavaProject[] { project }, new IClasspathContainer[] { container }, null);
        } else {
            Log.error("Invalid jar directory '" + path.toString() +"'");
        }
    }
    
    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return true;
    }
    
    @Override
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project,
            IClasspathContainer containerSuggestion) throws CoreException {
        JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },   new IClasspathContainer[] { containerSuggestion }, null);
    }
    
    @Override
    public Object getComparisonID(IPath containerPath, IJavaProject project) {
        return JarDirectoryContainer.ID + ":" + project.getProject().getName() + "/" + containerPath;
    }
}
