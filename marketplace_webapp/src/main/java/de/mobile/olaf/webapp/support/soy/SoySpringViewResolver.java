package de.mobile.olaf.webapp.support.soy;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

/**
 * View resolver for soy templates.
 * 
 * The viewname must follow the convention
 * {fileName}.{templateName}
 * 
 * e.g.:
 * 
 * de.mobile.coma.Index:coma.cs.main compiles the soy file "de/mobile/coma/Index.soy" and renders the template "coma.cs.main".
 * 
 * You can define a basepackage in the form 'de.mobile.coma'. 
 * 
 * @author apeglow
 *
 */
public class SoySpringViewResolver implements ViewResolver {
	private final String basePackage;
	
	private final Map<String, SoyTofu> cache = new ConcurrentHashMap<String, SoyTofu>();
	
	public SoySpringViewResolver(){
		this("");
	}
	
	public SoySpringViewResolver(String basePackage){
		basePackage = basePackage.replaceAll("\\.", "/");
		if (!basePackage.endsWith("/")){
			basePackage = basePackage+"/";
		}
		
		if (!basePackage.startsWith("/")){
			basePackage = "/" + basePackage;
		}
		
		this.basePackage = basePackage;
	}
	

	@Override
	public View resolveViewName(String viewName, Locale locale) {
		int dotPos = viewName.indexOf(":");
		if (dotPos<0){
			return null;
		}
		
		String fileName = basePackage+viewName.substring(0, dotPos)+".soy";
		String templateName = viewName.substring(dotPos+1);
		
		SoyTofu soyTofu = cache.get(fileName);
		if (soyTofu == null) {
			URL viewUrl = getClass().getResource(fileName);
			if (viewUrl == null){
				return null;
			}
		
			SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();
	        SoyFileSet sfs = sfsBuilder.add(viewUrl).build();
	        soyTofu = sfs.compileToJavaObj();
	        cache.put(fileName, soyTofu);
		}
        
        return new SoyView(soyTofu, templateName);
	}

}
