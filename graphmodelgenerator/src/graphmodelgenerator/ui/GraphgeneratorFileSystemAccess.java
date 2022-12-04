package graphmodelgenerator.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.util.RuntimeIOException;
import org.eclipse.xtext.util.StringInputStream;

public class GraphgeneratorFileSystemAccess extends EclipseResourceFileSystemAccess2 {
	
	@Override
	public void generateFile(String fileName, String outputName, CharSequence contents) {
		
		if (super.getMonitor().isCanceled())
			throw new OperationCanceledException();
		
		OutputConfiguration outputConfig = getOutputConfig(outputName);
		
		if (!ensureOutputConfigurationDirectoryExists(outputConfig))
			return;

		IFile file = getFile(fileName, outputName);
		if (file == null)
			return;

		try {
			String encoding = getEncoding(file);
			CharSequence postProcessedContent = postProcess(fileName, outputName, contents, encoding);
			String contentsAsString = postProcessedContent.toString();
			StringInputStream newContent = getInputStream(contentsAsString, encoding);
			generateFile(file, newContent, null, postProcessedContent, outputConfig);
		} catch (CoreException e) {
			throw new RuntimeIOException(e);
		}
	}
}
