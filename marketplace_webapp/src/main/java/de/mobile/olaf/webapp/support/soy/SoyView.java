package de.mobile.olaf.webapp.support.soy;

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

public class SoyView implements View {
	
	private final SoyTofu soyTofu;
	private final String templateName;
	
	public SoyView(SoyTofu soyTofu, String templateName){
		this.soyTofu = soyTofu;
		this.templateName = templateName;
	}

	@Override
	public String getContentType() {
		return "text/html;charset=utf-8";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SoyMapData soyMap = model == null ? new SoyMapData() : new SoyMapData(model);
        Writer responseWriter = response.getWriter();
        String bodyTemplate = soyTofu.render(templateName, soyMap, null);
        responseWriter.append(bodyTemplate);
        responseWriter.flush();
	}

}
