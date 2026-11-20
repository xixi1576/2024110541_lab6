package com.example.a2024110541lab6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 第 1 个 Fragment：个人信息。
 *
 * <p>个人信息对象 {@link Profile} 与历史记录列表都实现（或由可序列化容器承载）
 * {@link java.io.Serializable}，通过 {@link ObjectStore} 使用对象流写入文件，
 * 因此退出应用后重新打开可以恢复上次保存的内容与历史记录。</p>
 */
public class PersonalInfoFragment extends Fragment {

    /** 个人信息文件名。 */
    private static final String FILE_PROFILE = "profile.dat";
    /** 个人信息历史记录文件名。 */
    private static final String FILE_HISTORY = "profile_history.dat";
    /** 历史记录最多保存条数。 */
    private static final int MAX_HISTORY = 10;

    private EditText editName;
    private EditText editStudentId;
    private EditText editCollege;
    private EditText editMajor;
    private EditText editHobby;
    private TextView textHistory;

    private final ArrayList<ProfileRecord> history = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editName = view.findViewById(R.id.edit_name);
        editStudentId = view.findViewById(R.id.edit_student_id);
        editCollege = view.findViewById(R.id.edit_college);
        editMajor = view.findViewById(R.id.edit_major);
        editHobby = view.findViewById(R.id.edit_hobby);
        textHistory = view.findViewById(R.id.text_history);

        view.findViewById(R.id.button_save).setOnClickListener(v -> saveProfile());
        view.findViewById(R.id.button_clear_history).setOnClickListener(v -> clearHistory());

        restoreProfile();
        loadHistory();
        renderHistory();
    }

    /**
     * 从序列化文件中恢复个人信息；没有保存过则显示默认值。
     */
    private void restoreProfile() {
        Object data = ObjectStore.read(requireContext(), FILE_PROFILE);
        if (data instanceof Profile) {
            Profile profile = (Profile) data;
            editName.setText(profile.getName());
            editStudentId.setText(profile.getStudentId());
            editCollege.setText(profile.getCollege());
            editMajor.setText(profile.getMajor());
            editHobby.setText(profile.getHobby());
        } else {
            editName.setText("杨文辉");
            editStudentId.setText("2024110541");
            editCollege.setText("计算机科学学院");
            editMajor.setText("计算机科学与技术");
            editHobby.setText("编程、音乐");
        }
    }

    /**
     * 保存个人信息：序列化写入文件，并把本次保存追加到历史记录。
     */
    private void saveProfile() {
        long now = System.currentTimeMillis();
        Profile profile = new Profile(
                editName.getText().toString().trim(),
                editStudentId.getText().toString().trim(),
                editCollege.getText().toString().trim(),
                editMajor.getText().toString().trim(),
                editHobby.getText().toString().trim());
        profile.setSavedAt(now);
        ObjectStore.write(requireContext(), FILE_PROFILE, profile);

        history.add(0, new ProfileRecord(now, profile.getName(), profile.getStudentId(),
                profile.getCollege(), profile.getMajor()));
        while (history.size() > MAX_HISTORY) {
            history.remove(history.size() - 1);
        }
        ObjectStore.write(requireContext(), FILE_HISTORY, history);

        renderHistory();
        Toast.makeText(requireContext(),
                getString(R.string.profile_saved_format, dateFormat.format(new Date(now))),
                Toast.LENGTH_SHORT).show();
    }

    private void clearHistory() {
        history.clear();
        ObjectStore.write(requireContext(), FILE_HISTORY, history);
        renderHistory();
    }

    @SuppressWarnings("unchecked")
    private void loadHistory() {
        Object data = ObjectStore.read(requireContext(), FILE_HISTORY);
        if (data instanceof ArrayList) {
            history.clear();
            history.addAll((ArrayList<ProfileRecord>) data);
        }
    }

    private void renderHistory() {
        if (history.isEmpty()) {
            textHistory.setText(R.string.history_empty);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (ProfileRecord record : history) {
            builder.append(getString(R.string.profile_history_item_format,
                            dateFormat.format(new Date(record.getTime())),
                            record.getName(),
                            record.getStudentId(),
                            record.getCollege(),
                            record.getMajor()))
                    .append('\n');
        }
        textHistory.setText(builder.toString().trim());
    }
}
