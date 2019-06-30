/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.mapper;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.qualifier.QualifiedType;
import org.jdbi.v3.meta.Beta;

import static java.util.stream.Collectors.toSet;

/**
 * Column mapper that handles any {@link Nonnull} {@link QualifiedType}.
 *
 * {@code @Nonnull} is stripped from the received qualified type,
 * the actual column mapper for the remaining qualified type is resolved,
 * and the mapped value is checked, throwing {@link NullPointerException} if null.
 *
 * This allows you to query for any column type and make {@code null} explicitly forbidden as a result,
 * for when declarative programming is your cup of tea.
 */
@Beta
public class NonnullColumnMapperFactory implements QualifiedColumnMapperFactory {
    @Override
    public Optional<ColumnMapper<?>> build(QualifiedType<?> type, ConfigRegistry config) {
        if (type.hasQualifier(Nonnull.class)) {
            return lookup(type, config);
        } else {
            return Optional.empty();
        }
    }

    private static <T> Optional<ColumnMapper<?>> lookup(QualifiedType<T> type, ConfigRegistry config) {
        Set<Annotation> allExceptNonnull = type.getQualifiers().stream()
            .filter(x -> !(x instanceof Nonnull))
            .collect(toSet());

        return config.get(ColumnMappers.class).findFor(type.withAnnotations(allExceptNonnull))
            .map(mapper -> (r, i, ctx) -> Objects.requireNonNull(mapper.map(r, i, ctx), "type annotated with @Nonnull got a null value"));
    }
}
