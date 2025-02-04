/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.initializer.controller;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

import com.alibaba.initializer.metadata.ArchitectureCapability;
import com.alibaba.initializer.metadata.InitializerMetadata;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.mapper.InitializrMetadataV2JsonMapper;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
public class InitializerMetadataV2JsonMapper extends InitializrMetadataV2JsonMapper {

    public InitializerMetadataV2JsonMapper(String share) {
        super();
        TemplateVariables templateVariables = new TemplateVariables(
                new TemplateVariable("dependencies", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("packaging", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("javaVersion", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("language", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("bootVersion", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("groupId", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("artifactId", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("version", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("name", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("description", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("packageName", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("architecture", TemplateVariable.VariableType.REQUEST_PARAM));
        try {
            Field templateVariablesField = InitializrMetadataV2JsonMapper.class.getDeclaredField("templateVariables");
            templateVariablesField.setAccessible(true);
            templateVariablesField.set(this, templateVariables);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String write(InitializrMetadata metadata, String appUrl) {
        InitializerMetadata initializerMetadata = (InitializerMetadata) metadata;
        ObjectNode delegate = nodeFactory().objectNode();
        links(delegate, metadata.getTypes().getContent(), appUrl);
        dependencies(delegate, metadata.getDependencies());
        type(delegate, metadata.getTypes());
        architecture(delegate, initializerMetadata.getArchitecture());
        singleSelect(delegate, metadata.getPackagings());
        singleSelect(delegate, metadata.getJavaVersions());
        singleSelect(delegate, metadata.getLanguages());
        singleSelect(delegate, metadata.getBootVersions());
        text(delegate, metadata.getGroupId());
        text(delegate, metadata.getArtifactId());
        text(delegate, metadata.getVersion());
        text(delegate, metadata.getName());
        text(delegate, metadata.getDescription());
        text(delegate, metadata.getPackageName());
        return delegate.toString();
    }

    protected void architecture(ObjectNode parent, ArchitectureCapability capability) {
        ObjectNode single = nodeFactory().objectNode();
        single.put("type", capability.getType().getName());
        DefaultMetadataElement defaultType = capability.getDefault();
        if (defaultType != null) {
            single.put("default", defaultType.getId());
        }
        ArrayNode values = nodeFactory().arrayNode();
        values.addAll(capability.getContent().stream().map(this::mapValue)
                .collect(Collectors.toList()));
        single.set("values", values);
        parent.set(capability.getId(), single);
    }

}
