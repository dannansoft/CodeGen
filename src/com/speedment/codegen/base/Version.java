package com.speedment.codegen.base;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public abstract class Version<T extends Version<T>> {

	private final Map<Class<? extends CodeModel>, Class<? extends CodeView>> 
			modelToView = new HashMap<>();

	protected <M extends CodeModel, V extends CodeView<M>>
			T install(Class<M> model, Class<V> view) {
		modelToView.put(model, view);
		return (T) this;
	}

	public CodeView get(Class<? extends CodeModel> model) {
		return create(viewOf(model));
	}
	
	private Class<? extends CodeView> viewOf(Class<? extends CodeModel> model) {
		final Class<? extends CodeView> result = modelToView.get(model);
		
		if (result == null) {
			for (Map.Entry<
					Class<? extends CodeModel>, 
					Class<? extends CodeView>
				> e : modelToView.entrySet()) {
				if (e.getKey().isAssignableFrom(model)) {
					return e.getValue();
				}
			}
		} else {
			return result;
		}
		
		throw new UnsupportedOperationException(
			"Attemting to view the model '" + model.getName() + 
			"' that is not associated in the current Version." +
			"Please make sure there exists a view for each model " + 
			"and that they have been properly installed to the " +
			"class extending Version."
		);
	}
	
	public static <T> T create(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(Version.class.getName()).log(Level.SEVERE, 
				"The class '" + clazz.getName() + 
				"' could not be instantiated using the default constructor. " +
				"Make sure it is the correct class and that the default " +
				"constructor has been properly defined without no parameters.", ex);
		}
		
		return null;
	}
}