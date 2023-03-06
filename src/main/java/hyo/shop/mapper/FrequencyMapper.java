package hyo.shop.mapper;

import hyo.shop.domain.Frequency;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrequencyMapper {
    Frequency getFrequency(Frequency frequency);
    int insertFrequency(Frequency frequency);
    int updateFrequency(Frequency frequency);
    List<Frequency> getFrequencyList(Frequency frequency);
}
