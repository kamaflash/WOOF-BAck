/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.notificationservice.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Pc
 */

@Data
@NoArgsConstructor
public class StandarizedApiExeptionResponse {
    
    @Schema(description = "", name="type", requiredMode = Schema.RequiredMode.REQUIRED, example = "errors/authentication/not-autorized")
    private String type;
    @Schema(description = "", name="title", requiredMode = Schema.RequiredMode.REQUIRED, example = "errors/authentication/not-autorized")
    private String title;
    @Schema(description = "", name="code", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "errors/authentication/not-autorized")
    private String code;
    @Schema(description = "", name="datail", requiredMode = Schema.RequiredMode.REQUIRED, example = "errors/authentication/not-autorized")
    private String datail;
    @Schema(description = "", name="instance", requiredMode = Schema.RequiredMode.REQUIRED, example = "errors/authentication/not-autorized")
    private String instance;
    
    public StandarizedApiExeptionResponse(String type,String title,String code,String datail,String instance) {
        super();
        this.type = type;
        this.title = title;
        this.code = code;
        this.datail = datail;
        this.instance = instance;

}
}
