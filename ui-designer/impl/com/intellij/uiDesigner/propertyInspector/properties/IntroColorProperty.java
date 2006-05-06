package com.intellij.uiDesigner.propertyInspector.properties;

import com.intellij.openapi.util.Comparing;
import com.intellij.uiDesigner.XmlWriter;
import com.intellij.uiDesigner.lw.ColorDescriptor;
import com.intellij.uiDesigner.propertyInspector.IntrospectedProperty;
import com.intellij.uiDesigner.propertyInspector.PropertyEditor;
import com.intellij.uiDesigner.propertyInspector.PropertyRenderer;
import com.intellij.uiDesigner.propertyInspector.editors.ColorEditor;
import com.intellij.uiDesigner.propertyInspector.renderers.ColorRenderer;
import com.intellij.uiDesigner.radComponents.RadComponent;
import com.intellij.uiDesigner.snapShooter.SnapshotContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.Color;
import java.lang.reflect.Method;

/**
 * @author yole
 */
public class IntroColorProperty extends IntrospectedProperty<ColorDescriptor> {
  private ColorRenderer myColorRenderer = null;
  private ColorEditor myColorEditor = null;
  @NonNls private static final String CLIENT_PROPERTY_KEY_PREFIX = "IntroColorProperty_";

  public IntroColorProperty(final String name, final Method readMethod, final Method writeMethod, final boolean storeAsClient) {
    super(name, readMethod, writeMethod, storeAsClient);
  }

  @NotNull public PropertyRenderer<ColorDescriptor> getRenderer() {
    if (myColorRenderer == null) {
      myColorRenderer = new ColorRenderer();
    }
    return myColorRenderer;
  }

  @Nullable public PropertyEditor<ColorDescriptor> getEditor() {
    if (myColorEditor == null) {
      myColorEditor = new ColorEditor(getName());
    }
    return myColorEditor;
  }

  public void write(@NotNull ColorDescriptor value, XmlWriter writer) {
    writer.writeColorDescriptor(value);
  }

  @Override public ColorDescriptor getValue(final RadComponent component) {
    final ColorDescriptor colorDescriptor = (ColorDescriptor)component.getDelegee().getClientProperty(CLIENT_PROPERTY_KEY_PREFIX + getName());
    if (colorDescriptor == null) {
      return new ColorDescriptor((Color) invokeGetter(component));
    }
    return colorDescriptor;
  }

  @Override protected void setValueImpl(final RadComponent component, final ColorDescriptor value) throws Exception {
    component.getDelegee().putClientProperty(CLIENT_PROPERTY_KEY_PREFIX + getName(), value);
    if (value != null && value.isColorSet()) {
      invokeSetter(component, value.getResolvedColor());
    }
  }

  @Override public void resetValue(RadComponent component) throws Exception {
    super.resetValue(component);
    component.getDelegee().putClientProperty(CLIENT_PROPERTY_KEY_PREFIX + getName(), null);
  }

  @Override
  public void importSnapshotValue(final SnapshotContext context, final JComponent component, final RadComponent radComponent) {
    try {
      if (component.getParent() != null) {
        Color componentColor = (Color) myReadMethod.invoke(component, EMPTY_OBJECT_ARRAY);
        Color parentColor = (Color) myReadMethod.invoke(component.getParent(), EMPTY_OBJECT_ARRAY);
        if (componentColor != null && !Comparing.equal(componentColor, parentColor)) {
          setValue(radComponent, new ColorDescriptor(componentColor));
        }
      }
    }
    catch (Exception e) {
      // ignore
    }
  }
}
