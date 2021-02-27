package com.bochkov.smallcraft.wicket.web.error;

import com.giffing.wicket.spring.boot.context.scan.WicketExpiredPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

@WicketExpiredPage
public class ExpiredPage extends WebPage{

	@Override
	protected void setHeaders(final WebResponse response)
	{
		response.setStatus(HttpServletResponse.SC_GONE);
	}

}
