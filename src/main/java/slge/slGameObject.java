package slge;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
    A component needed to render a GameObject. GameObject "Has A" slComponent.
    [coordinates + slComponent object + name string] == Game Object --> A "Render-able"
    i.e. it has everything needed to submit itself to be rendered by a shader
*/
public class slGameObject {
    private String obj_name;
    private List<slComponent> list_components;
    public slVectorTransformer vector_transformer;
    public slGameObject(String name) {
        this.obj_name = name;
        this.list_components = new ArrayList<>();
        this.vector_transformer = new slVectorTransformer();
    }

    public slGameObject(String name, slVectorTransformer my_transformer) {
        this.obj_name = name;
        this.list_components = new ArrayList<>();
        this.vector_transformer = my_transformer;
    }

    public <T extends slComponent> T getComponent(Class<T> slComponentClass) {
        for (slComponent my_c : list_components) {
            if (slComponentClass.isAssignableFrom(my_c.getClass())) {
                try {
                    return slComponentClass.cast(my_c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error in casting components in list_components - bailing out :-(!";
                }
            }
        }

        return null;
    }

    public <T extends slComponent> void removeComponents(Class<T> slComponentClass) {
        for (int i = 0; i < list_components.size(); ++i) {
            slComponent my_c = list_components.get(i);
            if (slComponentClass.isAssignableFrom(my_c.getClass())) {
                list_components.remove(i);
                return;
            }
        }
    }

    public void addComponent(slComponent my_c) {
        this.list_components.add(my_c);
        // add a reference to the parent Component:
        my_c.thisGO = this;
    }

    public void update(float time_delta) {
        for (int i=0; i < list_components.size(); ++i) {
            list_components.get(i).update(time_delta);
        }
    }

    public void start() {
        for (int i=0; i < list_components.size(); ++i) {
            list_components.get(i).start();
        }
    }


}  // public class slGameObject
