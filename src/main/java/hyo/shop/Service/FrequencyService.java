package hyo.shop.Service;

import hyo.shop.domain.Frequency;
import hyo.shop.mapper.FrequencyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FrequencyService {

    private final FrequencyMapper frequencyMapper;

    public Frequency getFrequency(Frequency frequency) {
        return frequencyMapper.getFrequency(frequency);
    }

    public int insertFrequency(Frequency frequency) {
        return frequencyMapper.insertFrequency(frequency);
    }

    public int updateFrequency(Frequency frequency) {
        return frequencyMapper.updateFrequency(frequency);
    }

    public List<Frequency> getFrequencyList(Frequency frequency) {
        return frequencyMapper.getFrequencyList(frequency);
    }
}
