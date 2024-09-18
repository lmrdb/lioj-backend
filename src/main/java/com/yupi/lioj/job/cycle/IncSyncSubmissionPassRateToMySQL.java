package com.yupi.lioj.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.lioj.mapper.QuestionMapper;
import com.yupi.lioj.mapper.QuestionSubmitMapper;
import com.yupi.lioj.model.entity.Question;
import com.yupi.lioj.model.entity.QuestionSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IncSyncSubmissionPassRateToMySQL {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    /**
     * 每天执行一次
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void run() {
        // 统计每道题目的总提交数
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("questionId", "count(*) as submitNum").groupBy("questionId");
        List<Map<String, Object>> questionSubmitsCountList = questionSubmitMapper.selectMaps(queryWrapper);

        // 统计每道题目的通过数（Accepted 的提交数）
        QueryWrapper<QuestionSubmit> queryWrapperAccepted = new QueryWrapper<>();
        queryWrapperAccepted.select("questionId", "count(*) as acceptedNum")
                .like("judgeInfo", "Accepted")  // 假设 judgeResult 字段包含判题结果
                .groupBy("questionId");
        List<Map<String, Object>> questionAcceptedCountList = questionSubmitMapper.selectMaps(queryWrapperAccepted);

        // 将通过数结果存入 Map，方便快速查找
        Map<Long, Integer> acceptedCountMap = new HashMap<>();
        for (Map<String, Object> acceptedCount : questionAcceptedCountList) {
            Long questionId = (Long) acceptedCount.get("questionId");
            Integer acceptedNum = Integer.valueOf(acceptedCount.get("acceptedNum").toString());
            acceptedCountMap.put(questionId, acceptedNum);
        }

        // 更新题目表的提交数和通过数
        for (Map<String, Object> submitCount : questionSubmitsCountList) {
            Long questionId = (Long) submitCount.get("questionId");
            Integer submitNum = Integer.valueOf(submitCount.get("submitNum").toString());
            Integer acceptedNum = acceptedCountMap.getOrDefault(questionId, 0); // 如果没有通过数，默认为 0

            // 更新题目
            Question question = new Question();
            question.setId(questionId);
            question.setSubmitNum(submitNum);
            question.setAcceptedNum(acceptedNum);
            questionMapper.updateById(question);
        }

        log.info("题目提交数和通过数已同步更新");
    }
}
