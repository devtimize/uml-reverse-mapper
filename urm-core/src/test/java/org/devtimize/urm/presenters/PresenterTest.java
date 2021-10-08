package org.devtimize.urm.presenters;

import static org.junit.Assert.assertTrue;


import org.junit.Test;

public class PresenterTest {

  @Test
  public void parseShouldReturnCorrectPresenter() {
    Presenter presenter = Presenter.parse("graphviz");
    assertTrue(presenter.getClass().getSimpleName().equals("GraphvizPresenter"));
    presenter = Presenter.parse("plantuml");
    assertTrue(presenter.getClass().getSimpleName().equals("PlantUMLPresenter"));
  }
}
