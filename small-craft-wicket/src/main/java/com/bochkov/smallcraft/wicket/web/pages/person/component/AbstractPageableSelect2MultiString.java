package com.bochkov.smallcraft.wicket.web.pages.person.component;

import com.google.common.collect.Streams;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Accessors(chain = true)
public abstract class AbstractPageableSelect2MultiString extends AbstractSelect2MultiString {

    @Getter
    @Setter
    boolean addQueryToResult = true;

    public AbstractPageableSelect2MultiString(String id) {
        super(id);
    }

    public AbstractPageableSelect2MultiString(String id, IModel<Collection<String>> model) {
        super(id, model);
    }

    @Override
    public void query(String term, int page, Response<String> response) {
        Page<String> pg = query(term, PageRequest.of(page, 10));
        if (addQueryToResult) {
            response.setResults(Streams.concat(Stream.of(term), pg.stream()).collect(Collectors.toList()));
        } else {
            response.setResults(pg.getContent());
        }
        response.setHasMore(pg.getTotalPages() > page);
    }

    @Override
    protected void onInitialize() {
        getSettings().setTheme("bootstrap4");
        super.onInitialize();
    }

    public abstract Page<String> query(String term, Pageable pageable);


}
