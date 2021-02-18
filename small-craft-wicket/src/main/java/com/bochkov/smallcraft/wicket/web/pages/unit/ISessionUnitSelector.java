package com.bochkov.smallcraft.wicket.web.pages.unit;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public interface ISessionUnitSelector {

    public default void setAllFormModels(Long unit, Form form) {
        form.visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
            @Override
            public void component(FormComponent<?> cmp, IVisit<Object> visit) {
                if (cmp instanceof ISessionUnitSelector) {
                    ((ISessionUnitSelector) cmp).setIdUnitToModel(unit);
                }
            }
        });
        setIdUnitToSession(unit);
    }

    default Optional<Long> getIdUnitFromSession() {
        return loadCookie(RequestCycle.get().getRequest(), "id_unit")
                .stream()
                .map(Cookie::getValue)
                .map(value -> Application.get().getConverterLocator().getConverter(Long.class).convertToObject(value, Session.get().getLocale()))
                .filter(Objects::nonNull)
                .findFirst();
    }

    default void setIdUnitToSession(Long idUnit) {
        Response response = RequestCycle.get().getResponse();
        saveCookie(response, "id_unit", Optional.ofNullable(idUnit).map(u -> Application.get().getConverterLocator().getConverter(Long.class).convertToString(u, Session.get().getLocale())).orElse(null), 30);
        //Session.get().setAttribute("id_unit", idUnit);
    }

    default List<Cookie> loadCookie(Request request, String cookieName) {
        List<Cookie> cookies = ((WebRequest) request).getCookies();

        if (cookies == null) {
            return null;
        }

        return cookies.stream().filter(c -> "id_unit".equals(c.getName())).collect(Collectors.toList());
    }

    default void saveCookie(Response response, String cookieName, String cookieValue, int expiryTimeInDays) {
        removeCookieIfPresent(RequestCycle.get().getRequest(), response, cookieName);
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setSecure(true);
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(expiryTimeInDays));
        ((WebResponse) response).addCookie(cookie);
    }

    default void removeCookieIfPresent(Request request, Response response, String cookieName) {
        loadCookie(request, cookieName).forEach(cookie -> ((WebResponse) response).clearCookie(cookie));
    }

    public void setIdUnitToModel(Long idUnit);
}
