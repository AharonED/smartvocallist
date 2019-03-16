package Model;

import DataObjects.BaseModelObject;

public interface IModel {
    static  void addNew(BaseModelObject instance) throws Exception {
        throw new Exception("Not Implemented");
    }

    static  BaseModelObject get(Object database, String byId) throws Exception {
        throw new Exception("Not Implemented");
    }
    //static abstract void addNew(BaseModelObject instance);
    //BaseModelObject get(Object database, String byId);
}
