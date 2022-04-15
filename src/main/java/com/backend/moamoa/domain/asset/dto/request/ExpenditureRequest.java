package com.backend.moamoa.domain.asset.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpenditureRequest {

    private int fixed;

    private int variable;

}
