package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SessionUnitBehavior<C extends FormComponent<Long> & IIdUnitSelect> extends AjaxFormComponentUpdatingBehavior {

    static String COOKIENAME = "id_unit";

    /*CookieGenerator cookieGenerator = new CookieGenerator();*/

    C component;


    public SessionUnitBehavior() {
        super("change");
    }

    CookieGenerator cookieGenerator() {
        CookieGenerator cookieGenerator = new CookieGenerator();
        cookieGenerator.setCookieName(COOKIENAME);
        cookieGenerator.setCookieMaxAge((int) TimeUnit.DAYS.toSeconds(30));
        return cookieGenerator;
    }


    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);

    }

    protected void onComponentRendered() {
        putInUnitToCookie(component.getIdUnit());
    }

    @Override
    protected void onBind() {
        this.component = (C) getComponent();
        if (component.getConvertedInput() == null) {
            component.setIdUnit(getIdUnitFromCookie());
        }
    }

    public void putInUnitToCookie(Long id) {
        String value = Optional.ofNullable(id).map(i -> component.getConverter(Long.class).convertToString(i, Session.get().getLocale())).orElse(null);
        cookieGenerator().addCookie((HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse(), value);
    }

    public Long getIdUnitFromCookie() {
        Cookie cookie = WebUtils.getCookie((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest(), COOKIENAME);
        Long id = Optional.ofNullable(cookie).map(Cookie::getValue).map(str -> component.getConverter(Unit.class).convertToObject(str, Session.get().getLocale())).map(Unit::getId).orElse(null);
        return id;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        Long idUnit = component.getIdUnit();
        component.getForm().visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
            @Override
            public void component(FormComponent<?> cmp, IVisit<Object> visit) {
                if (cmp instanceof IIdUnitSelect) {
                    if (cmp.getOutputMarkupId()) {
                        target.add(cmp);
                        ((IIdUnitSelect) cmp).setIdUnit(idUnit);
                    }
                }
            }
        });
    }
}
