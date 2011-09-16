package org.antifun.eclipsejardirectory;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JarDirectoryContainerPage extends WizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension {

    private IJavaProject project;
    private Text directoryAsText;
    private Button browseButton;
    private IPath chosenPath;
    
    public JarDirectoryContainerPage() {
        super("Choose Jar Directory", "Jar Directory", null);        
    }
    
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());
        createDirGroup(composite);
        setControl(composite);
    }

    private void createDirGroup(Composite parent) {
        Composite dirSelectionGroup = new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.numColumns = 3;
        dirSelectionGroup.setLayout(layout);
        GridData gridData = new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL);
              
        dirSelectionGroup.setLayoutData(gridData);

        new Label(dirSelectionGroup, SWT.NONE).setText("Directory (project-relative):");

        directoryAsText = new Text(dirSelectionGroup, SWT.BORDER | SWT.READ_ONLY);        
        directoryAsText.setText( JarDirectoryContainer.getProjectRelativePath(JarDirectoryContainer.getAbsolutePath(project, chosenPath), project).toString() );
        GridData directoryLayoutData = new GridData(GridData.GRAB_HORIZONTAL);
        GC gc = new GC(directoryAsText);
        FontMetrics fm = gc.getFontMetrics();
        directoryLayoutData.horizontalAlignment = SWT.FILL;
        directoryLayoutData.widthHint = fm.getAverageCharWidth() * 40;
        directoryAsText.setLayoutData(directoryLayoutData);
        gc.dispose();
        
        browseButton = new Button(dirSelectionGroup, SWT.PUSH);
        browseButton.setText("Browse..."); 
        browseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowseButtonPressed();
           }

        });    
        dirSelectionGroup.pack();
        setControl(dirSelectionGroup);
    }

    private void handleBrowseButtonPressed() {
        DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setMessage("Choose Jar Directory");
        dialog.setFilterPath(project.getPath().makeAbsolute().toString());
        String dir = dialog.open();
        if (dir != null) {
            // dir is an absolute path; the project path must be a prefix of it. if it is, then the following will succeed.
            IPath projectRelativePath = JarDirectoryContainer.getProjectRelativePath(new Path(dir), project);
            if (projectRelativePath != null) {
                directoryAsText.setText(projectRelativePath.toString());
                chosenPath = JarDirectoryContainer.getAbsolutePath(project, projectRelativePath);
                setPageComplete(true);
                setErrorMessage(null);
            } else {
                directoryAsText.setText("");
                chosenPath = null;
                setPageComplete(false);
                setErrorMessage("You must choose a subdirectory of the project root.");
            }
        }   
    }

    @Override
    public void initialize(IJavaProject project, IClasspathEntry[] classpathEntries) {
        this.project = project;
        this.chosenPath = null;
    }

    @Override
    public boolean finish() {
        return true;
    }

    @Override
    public IClasspathEntry getSelection() {
        // this isn't right either
        IPath containerPath = new Path(JarDirectoryContainer.ID).append( "/" + JarDirectoryContainer.getProjectRelativePath(chosenPath, project) );
        return JavaCore.newContainerEntry(containerPath);
    }

    @Override
    public void setSelection(IClasspathEntry entry) {
        if (entry != null) {
            this.chosenPath = JarDirectoryContainer.getAbsolutePath(project, JarDirectoryContainer.getRelativePath(entry.getPath()));
            if (this.directoryAsText != null) {
                this.directoryAsText.setText(JarDirectoryContainer.getRelativePath(entry.getPath()).toString());
            }
        } 
    }
}
