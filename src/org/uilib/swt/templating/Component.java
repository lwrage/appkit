package org.uilib.swt.templating;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.templating.components.UIController;

public final class Component {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String name;
	private final String type;
	private final ImmutableList<Component> children;
	private final ImmutableMultimap<String, Component> nameMap;
	private final UIController controller;
	private final Options options;
	private Control control;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Component(final String name, final String type, final List<Component> children,
					 final UIController controller, final Options options) {
		this.name = name;

		// FIXME: Templating: a-z,-, / , alles klein
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(children);
		Preconditions.checkNotNull(controller);
		Preconditions.checkNotNull(options);

		this.type		    = type;
		this.children	    = ImmutableList.copyOf(children);
		this.controller     = controller;
		this.options	    = options;

		ImmutableMultimap.Builder<String, Component> map = ImmutableMultimap.builder();

		map.put("$" + type, this);
		if (this.name != null) {
			map.put(name, this);
			map.put(name + "$" + type, this);
		}

		for (final Component child : this.children) {
			map.putAll(child.getNameMap());

			if (this.name != null) {
				for (final String key : child.getNameMap().keySet()) {

					// FIXME: Predicate
					if (! key.contains("$")) {
						map.putAll(name + "." + key, child.getNameMap().get(key));
					}
				}
			}
		}
		this.nameMap = map.build();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public ImmutableMultimap<String, Component> getNameMap() {
		return this.nameMap;
	}

	public Control getControl() {
		return this.control;
	}

	public UIController getController() {
		return this.controller;
	}

	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public ImmutableList<Component> getChildren() {
		return this.children;
	}

	public Options getOptions() {
		return this.options;
	}

	public Control get(final String query) {

		ImmutableCollection<Component> controls = this.nameMap.get(query);

		Preconditions.checkState(controls.size() == 1, "found " + controls.size() + " controls for '" + query + "'");

		return controls.iterator().next().getControl();
	}

	public void initialize(final Composite parent) {
		Preconditions.checkArgument(this.control == null, "control wasn't null -> double initialization");

		this.control = this.controller.initialize(parent, this.options);

		Preconditions.checkState(
			this.children.isEmpty() || (this.control instanceof Composite),
			"compnent has children, but ui-controller didn't return a composite");
		if (this.children.isEmpty()) {
			return;
		}

		/* create children */
		Composite comp = (Composite) this.control;
		comp.setLayout(new GridLayout(-1, false));
		for (final Component child : this.children) {
			child.initialize(comp);
			child.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}

		/* layout columns */
		String columns = this.options.get("columns", "1");
		if (columns.equals("variable")) {
			((GridLayout) comp.getLayout()).numColumns = this.children.size();
		} else {
			((GridLayout) comp.getLayout()).numColumns = Integer.valueOf(columns);
		}
	}
}