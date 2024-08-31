package com.yupi.lioj.service;

import com.yupi.lioj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.lioj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.lioj.model.entity.User;

/**
* @author 19799
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-08-29 16:26:28
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

}
