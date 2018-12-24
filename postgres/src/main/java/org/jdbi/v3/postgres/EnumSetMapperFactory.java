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
package org.jdbi.v3.postgres;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Optional;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;

public class EnumSetMapperFactory implements ColumnMapperFactory {
    @Override
    public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry config) {
        Class<?> erasedType = GenericTypes.getErasedType(type);

        // TODO never matches, my guess is the collection type is unwrapped to be collected into later
        // TODO we might need to use a qualifier for all this "special postgres enum handling" stuff, actually
        if (erasedType != EnumSet.class) {
            return Optional.empty();
        }

        Type enumType = GenericTypes.findGenericParameter(type, EnumSet.class)
            .orElseThrow(() -> new IllegalArgumentException("No generic information for " + type));

        if (Enum.class.isAssignableFrom((Class<?>) enumType)) {
            @SuppressWarnings("unchecked")
            Class<Enum> enumClass = (Class<Enum>) enumType;
            return Optional.of(new EnumSetColumnMapper<>(enumClass));
        } else {
            throw new IllegalArgumentException("Generic type of " + type + " is not an enum");
        }
    }
}
