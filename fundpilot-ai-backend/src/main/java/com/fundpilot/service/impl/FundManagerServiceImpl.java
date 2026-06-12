package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fundpilot.entity.FundManager;
import com.fundpilot.mapper.FundManagerMapper;
import com.fundpilot.service.FundManagerService;
import com.fundpilot.vo.ManagerVO;
import org.springframework.stereotype.Service;

/**
 * 基金经理服务实现
 */
@Service
public class FundManagerServiceImpl extends ServiceImpl<FundManagerMapper, FundManager> implements FundManagerService {

    @Override
    public ManagerVO getManagerDetail(Long managerId) {
        FundManager manager = getById(managerId);
        if (manager == null) {
            return null;
        }
        return toVO(manager);
    }

    @Override
    public ManagerVO getManagerByFundCode(String fundCode) {
        return baseMapper.selectManagerByFundCode(fundCode);
    }

    private ManagerVO toVO(FundManager m) {
        ManagerVO vo = new ManagerVO();
        vo.setId(m.getId());
        vo.setName(m.getName());
        vo.setGender(m.getGender());
        vo.setCompany(m.getCompany());
        vo.setTenureYears(m.getTenureYears());
        vo.setEducation(m.getEducation());
        vo.setBiography(m.getBiography());
        vo.setInvestStyle(m.getInvestStyle());
        vo.setBestReturn(m.getBestReturn());
        vo.setManageCount(m.getManageCount());
        vo.setTotalManage(m.getTotalManage());
        vo.setStatus(m.getStatus());
        return vo;
    }
}
