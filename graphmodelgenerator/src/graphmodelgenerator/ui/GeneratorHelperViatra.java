package graphmodelgenerator.ui;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphmodelgenerator.generator.SysMLGeneratorViatra;

public class GeneratorHelperViatra extends AbstractHandler implements IHandler {

	public static final Logger logger = LoggerFactory.getLogger(GeneratorHelperViatra.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IFile) {
				IFile file = (IFile) firstElement;
				IProject project = file.getProject();
				String path = file.getFullPath().removeLastSegments(1).toString();
				path = StringUtils.join("src-gen", StringUtils.substringAfter(path, "src"));

				EclipseResourceFileSystemAccess2 fsa = new GraphgeneratorFileSystemAccess();
				fsa.setMonitor(new NullProgressMonitor());
				fsa.setProject(project);
				fsa.setOutputPath(path);
				
				Map<String, OutputConfiguration> fsaConfig = fsa.getOutputConfigurations();
				fsaConfig.values().forEach(config -> {
					config.setCreateOutputDirectory(true);
					config.setOverrideExistingResources(true);
				});
				
				URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
				
				ResourceSet resourceSet = new ResourceSetImpl();				
				Resource resource = resourceSet.getResource(uri, true);
				EcoreUtil.resolveAll(resourceSet);
				
				try {
					resource.load(null);
					if (resource.isLoaded()) {
						SysMLGeneratorViatra generator = new SysMLGeneratorViatra();
						generator.doGenerate(resource, fsa, new GeneratorContext());
						logger.info("Generation completed!");
					}
				} catch (IOException e) {
					logger.error("Couldn't generate graphmodel files!");
					logger.error(ExceptionUtils.getStackTrace(e));
				}			
			}
		}
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}