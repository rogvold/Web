package ru.cardio.filters;

import java.util.List;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
public interface FilterInterfaces {
    
    public List<Integer> filterRates(List<Integer> rates) throws CardioException;
}
