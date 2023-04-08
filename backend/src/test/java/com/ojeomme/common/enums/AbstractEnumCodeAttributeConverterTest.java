package com.ojeomme.common.enums;

import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractEnumCodeAttributeConverterTest {

    @Nested
    class convertToDatabaseColumn {

        @Test
        void enum을_DB_값으로_변환한다() {
            // given
            TestEnumConverter converter = new TestEnumConverter(false);
            TestEnum mockEnum = TestEnum.TEST_1;

            // when
            String code = converter.convertToDatabaseColumn(mockEnum);

            // then
            assertThat(code).isEqualTo(TestEnum.TEST_1.getCode());
        }

        @Test
        void enum을_DB_값으로_변환할_때_nullable이_false고_enum이_null이면_EnumCodeException를_발생한다() {
            // given
            TestEnumConverter converter = new TestEnumConverter(false);
            TestEnum nullEnum = null;

            // when
            ApiException exception = assertThrows(ApiException.class, () -> converter.convertToDatabaseColumn(nullEnum));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.ENUM_CODE);
        }

        @Test
        void enum을_DB_값으로_변환할_때_nullable이_true고_enum이_null이면_빈_문자열을_반환한다() {
            // given
            TestEnumConverter converter = new TestEnumConverter(true);
            TestEnum nullEnum = null;

            // when
            String code = converter.convertToDatabaseColumn(nullEnum);

            // then
            assertThat(code).isBlank();
        }
    }

    @Nested
    class convertToEntityAttribute {

        @Test
        void DB_값을_enum으로_변환한다() {
            // given
            TestEnumConverter converter = new TestEnumConverter(false);
            String mockCode = TestEnum.TEST_1.getCode();

            // when
            TestEnum testEnum = converter.convertToEntityAttribute(mockCode);

            // then
            assertThat(testEnum).isEqualTo(TestEnum.TEST_1);
        }

        @Test
        void DB_값을_enum으로_변환할_때_nullable이_false고_DB가_null_또는_blank면_EnumCodeException를_발생한다() {
            // given
            TestEnumConverter converter = new TestEnumConverter(false);
            String nullCode = null;
            String blankCode = "";

            // when
            ApiException exception1 = assertThrows(ApiException.class, () -> converter.convertToEntityAttribute(nullCode));
            ApiException exception2 = assertThrows(ApiException.class, () -> converter.convertToEntityAttribute(blankCode));

            // then
            assertThat(exception1.getErrorCode()).isEqualTo(ApiErrorCode.ENUM_CODE);
            assertThat(exception2.getErrorCode()).isEqualTo(ApiErrorCode.ENUM_CODE);
        }

        @Test
        void DB_값을_enum으로_변환할_때_nullable이_true고_DB가_null_또는_blank면_null을_반환한다() {
            // given
            TestEnumConverter converter = new TestEnumConverter(true);
            String nullCode = null;
            String blankCode = "";

            // when
            TestEnum nullEnum = converter.convertToEntityAttribute(nullCode);
            TestEnum BlankEnum = converter.convertToEntityAttribute(blankCode);

            // then
            assertThat(nullEnum).isNull();
            assertThat(BlankEnum).isNull();
        }
    }

    private enum TestEnum implements EnumCodeType {
        TEST_1("test1", "1");

        private final String desc;
        private final String code;

        TestEnum(String desc, String code) {
            this.desc = desc;
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    private static class TestEnumConverter extends AbstractEnumCodeAttributeConverter<TestEnum> {

        private static final String ENUM_NAME = "테스트";

        public TestEnumConverter(boolean nullable) {
            super(nullable, ENUM_NAME);
        }
    }
}