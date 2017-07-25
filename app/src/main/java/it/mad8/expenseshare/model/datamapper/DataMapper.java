package it.mad8.expenseshare.model.datamapper;

import it.mad8.expenseshare.model.Model;

/**
 * Created by giaco on 22/05/2017.
 */

public interface DataMapper<T extends Model> {
    T toModel();
}
