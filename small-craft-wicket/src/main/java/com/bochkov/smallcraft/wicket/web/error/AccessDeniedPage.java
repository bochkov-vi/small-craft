package com.bochkov.smallcraft.wicket.web.error;

import com.bochkov.smallcraft.wicket.web.BasePage;
import com.giffing.wicket.spring.boot.context.scan.WicketAccessDeniedPage;
import org.apache.wicket.request.http.WebResponse;
import org.wicketstuff.annotation.mount.MountPath;

import javax.servlet.http.HttpServletResponse;

@MountPath("problem")
@WicketAccessDeniedPage
public class AccessDeniedPage extends BasePage<Void> {


	@Override
	protected void setHeaders(final WebResponse response)
	{
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public boolean isErrorPage()
	{
		return true;
	}

	@Override
	public boolean isVersioned()
	{
		return false;
	}

}
