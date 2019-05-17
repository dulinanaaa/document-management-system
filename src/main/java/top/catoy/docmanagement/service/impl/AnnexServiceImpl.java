package top.catoy.docmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.Annex;
import top.catoy.docmanagement.mapper.AnnexMapper;
import top.catoy.docmanagement.service.AnnexService;


@Service
public class AnnexServiceImpl implements AnnexService {

    @Autowired
    private AnnexMapper annexMapper;

    @Override
    public int insertAnnex(Annex annex) {
        return annexMapper.insertAnnex(annex);
    }
}
