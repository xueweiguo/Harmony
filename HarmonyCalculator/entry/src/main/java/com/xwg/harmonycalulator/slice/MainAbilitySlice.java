package com.xwg.harmonycalulator.slice;

import com.xwg.harmonycalulator.CalculateEngine.CalculateEngine;
import com.xwg.harmonycalulator.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbPalette;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.components.element.StateElement;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;


public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MainAbilitySlice");
    private CalculateEngine calculator = null;
    private boolean finished = false;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        HiLog.warn(label, "AbilitySlice onStart!");

        ShapeElement elementButtonOn = new ShapeElement();
        elementButtonOn.setRgbColor(RgbPalette.GREEN);
        elementButtonOn.setShape(ShapeElement.RECTANGLE);

        ShapeElement elementButtonOff = new ShapeElement();
        elementButtonOff.setRgbColor(RgbPalette.WHITE);
        elementButtonOff.setShape(ShapeElement.RECTANGLE);

        StateElement checkElement = new StateElement();
        checkElement.addState(new int[]{ComponentState.COMPONENT_STATE_CHECKED}, elementButtonOn);
        checkElement.addState(new int[]{ComponentState.COMPONENT_STATE_EMPTY}, elementButtonOff);

        Checkbox checkbox = (Checkbox) findComponentById(ResourceTable.Id_check_box);
        checkbox.setButtonElement(checkElement);

        Button const_button = (Button)findComponentById(ResourceTable.Id_const_button);
        MainAbilitySlice This = this;
        const_button.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component v) {
                CustomListDialog dialog = new CustomListDialog(This);
                dialog.setTitle(ResourceTable.String_SelectConst);
                String[] items = calculator.getConstManager().consts().toArray(new String[0]);
                dialog.setItems(items);
                dialog.setListener(new CustomListDialog.IListListener() {
                    @Override
                    public void onSelect(int index, String text) {
                        appendQuestionString(text);
                    }
                });
                dialog.show();
            }
        });

        prepareDirectionButtons();
        prepareFunButtons();
        prepareCustomizeFunButtons();

        Button back_button = (Button)findComponentById(ResourceTable.Id_back_button);
        back_button.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component v) {
                backQuestion();
            }
        });

        Button ac_button = (Button)findComponentById(ResourceTable.Id_ac_button);
        ac_button.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component v) {
                clearQuestion();
            }
        });

        calculator = new CalculateEngine(getContext());
        Button calculate_button = (Button)findComponentById(ResourceTable.Id_calucate_button);
        calculate_button.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component v) {
                calculate();
            }
        });
    }

    private void showCommonDialog()
    {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setTitleText("$string:const_select_dialog");
        HiLog.warn(label, "setContentComponent!");
        Component  component = LayoutScatter.getInstance(this).parse(ResourceTable.Layout_list_dialog, null, true);
        dialog.setContentCustomComponent(component);
        dialog.setTransparent(true);
        dialog.show();
        Button button = (Button)(dialog.searchComponentViaId(ResourceTable.Id_ok_button));
        button.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component v) {
                HiLog.warn(label, "OK button clicked!");
                dialog.destroy();
            }
        });
    }

    private void showDialog() throws NotExistException, WrongTypeException, IOException {
        CustomListDialog dialog = new CustomListDialog(this);
        String[] items = {"Item1", "Item2", "Item3"};
        dialog.setItems(items);
        dialog.setListener(new CustomListDialog.IListListener() {
            @Override
            public void onSelect(int index, String text) {
                appendQuestionString(text);
            }
        });
        dialog.show();
    }

    @Override
    public void onActive() {
        HiLog.warn(label, "onActive!!!");
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        HiLog.warn(label, "onForeground!!!");
        super.onForeground(intent);
    }

    @Override
    protected void onResult(int requestCode, Intent resultIntent) {
        HiLog.warn(label, "onResult!!!");
        if (requestCode == 0) {
            // Process resultIntent here.
        }
    }

    private void prepareDirectionButtons(){
        int direct_button[] = {
                //token_area
                ResourceTable.Id_i_button,
                ResourceTable.Id_angle_button,
                ResourceTable.Id_degree_button,
                ResourceTable.Id_left_parentheses_button,
                ResourceTable.Id_comma_button,
                ResourceTable.Id_right_parentheses_button,
                ResourceTable.Id_sharp_button,

                //number_area
                ResourceTable.Id_number0_button,
                ResourceTable.Id_number1_button,
                ResourceTable.Id_number2_button,
                ResourceTable.Id_number3_button,
                ResourceTable.Id_number4_button,
                ResourceTable.Id_number5_button,
                ResourceTable.Id_number6_button,
                ResourceTable.Id_number7_button,
                ResourceTable.Id_number8_button,
                ResourceTable.Id_number9_button,
                ResourceTable.Id_plus_button,
                ResourceTable.Id_minus_button,
                ResourceTable.Id_mul_button,
                ResourceTable.Id_div_button,
                ResourceTable.Id_dot_button,
                ResourceTable.Id_exp_button,
                ResourceTable.Id_percent_button,
        };
        for(int id:direct_button){
            Button button = (Button) findComponentById(id);
            button.setClickedListener(new Component.ClickedListener() {
                public void onClick(Component v) {
                    appendQuestionString(((Button)v).getText());
                }
            });
        }
    }

    private void prepareFunButtons(){
        int std_fun_button[] = {
            //fun_area1
            ResourceTable.Id_sin_button,
            ResourceTable.Id_cos_button,
            ResourceTable.Id_tan_button,
            ResourceTable.Id_asin_button,
            ResourceTable.Id_acos_button,
            ResourceTable.Id_atan_button,
            //fun_area2
            ResourceTable.Id_x2_button,
            ResourceTable.Id_x3_button,
            ResourceTable.Id_sqrt_button,
            ResourceTable.Id_subtriplicate_button,
            ResourceTable.Id_power_button,
            ResourceTable.Id_root_button,
        };
        for(int id:std_fun_button){
            Button button = (Button) findComponentById(id);
            button.setClickedListener(new Component.ClickedListener() {
                public void onClick(Component v) {
                    appendQuestionString(((Button)v).getText() + "(");
                }
            });
        }
    }

    private void prepareCustomizeFunButtons(){
        int customize_fun_button[] = {
            //fun_area3
            ResourceTable.Id_f1_button,
            ResourceTable.Id_f2_button,
            ResourceTable.Id_f3_button,
            ResourceTable.Id_f4_button,
            ResourceTable.Id_f5_button,
            ResourceTable.Id_f6_button,
        };
        for(int id:customize_fun_button){
            Button button = (Button) findComponentById(id);
            button.setClickedListener(new Component.ClickedListener() {
                public void onClick(Component v) {
                    appendQuestionString(((Button)v).getText() + "(");
                }
            });
        }
    }

    private void appendQuestionString(String str){
        TextField question = (TextField)findComponentById(ResourceTable.Id_question_field);
        if(finished){
            clearQuestion();
            finished = false;
        }
        question.setText(question.getText() + str);
    }

    private void backQuestion(){
        TextField question = (TextField)findComponentById(ResourceTable.Id_question_field);
        String current = question.getText();
        if(current.length() > 0)
            question.setText(current.substring(0, current.length() - 1));
    }

    private void clearQuestion(){
        TextField question = (TextField)findComponentById(ResourceTable.Id_question_field);
        question.setText("");
        TextField answer = (TextField)findComponentById(ResourceTable.Id_answer_field);
        answer.setText("0");
    }

    private void calculate(){
        TextField questionField = (TextField)findComponentById(ResourceTable.Id_question_field);
        TextField answerField = (TextField)findComponentById(ResourceTable.Id_answer_field);
        try {
            answerField.setText(calculator.calculate(questionField.getText(), false));
        } catch (NotExistException | WrongTypeException | IOException e) {
            e.printStackTrace();
            answerField.setText("System Error");
        }
        finished = true;
    }
}
