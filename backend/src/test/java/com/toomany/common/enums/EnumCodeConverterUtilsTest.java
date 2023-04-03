package com.toomany.common.enums;

import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumCodeConverterUtilsTest {

    @Nested
    class ofCode {

        @Test
        void code를_enum으로_변환한다() {
            // given
            String code = TestEnum.TEST_1.getCode();

            // when
            TestEnum testEnum = EnumCodeConverterUtils.ofCode(code, TestEnum.class);

            // then
            assertThat(testEnum).isEqualTo(TestEnum.TEST_1);
        }

        @Test
        void code를_enum으로_변환하는데_code가_null이거나_blank이면_null을_반환하다() {
            // given
            String nullCode = null;
            String blankCode = "";


            // when
            TestEnum nullEnum = EnumCodeConverterUtils.ofCode(nullCode, TestEnum.class);
            TestEnum blankEnum = EnumCodeConverterUtils.ofCode(blankCode, TestEnum.class);

            // then
            assertThat(nullEnum).isNull();
            assertThat(blankEnum).isNull();
        }

        @Test
        void code를_enum으로_변환하는데_존재하지_않는_code면_EnumCode을_발생한다() {
            // given
            String wrongCode = "-1";

            // when
            ApiException exception = assertThrows(ApiException.class, () -> EnumCodeConverterUtils.ofCode(wrongCode, TestEnum.class));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.ENUM_CODE);
        }
    }

    @Nested
    class toCode {

        @Test
        void enum을_code로_변환한다() {
            // given
            TestEnum testEnum = TestEnum.TEST_1;

            // when
            String code = EnumCodeConverterUtils.toCode(testEnum);

            // then
            assertThat(code).isEqualTo(testEnum.getCode());
        }

        @Test
        void enum을_code로_변환하는데_enum이_null이면_null을_반환한다() {
            // given
            TestEnum testEnum = null;

            // when
            String code = EnumCodeConverterUtils.toCode(testEnum);

            // then
            assertThat(code).isNull();
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
}