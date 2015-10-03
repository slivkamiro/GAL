package com.slivkam.graphdemonstrator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.slivkam.graphdemonstrator.presenters.DemoPresenter.Demonstrator;
import com.slivkam.graphdemonstrator.presenters.GraphPresenter.GraphEditor;
import com.slivkam.graphdemonstrator.presenters.Presenter.PresenterFactory;
import com.slivkam.graphdemonstrator.views.GraphDemonstratorView;

public class ViewsModule extends AbstractModule {

    @Override
    protected void configure() {

        this.bind(GraphEditor.class).to(GraphDemonstratorView.class);

        this.bind(Demonstrator.class).to(GraphDemonstratorView.class);

        this.install(new FactoryModuleBuilder()
                .build(PresenterFactory.class));

    }

    @Provides
    public GraphDemonstratorView providesGraphDemonstratorView(PresenterFactory presenterFactory) {
        return new GraphDemonstratorView(presenterFactory);
    }

}
