package com.darian.ecommerce.product.entity;

import com.darian.ecommerce.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_edit_history")
public class ProductEditHistory {
    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edit_id")
    private Long id;

    // Product being edited (1-* relationship)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "editor_user_id")
    private User editor;

    // Date of the edit
    @Column(name = "edit_timestamp")
    private LocalDateTime editDate;

    // Description of changes made
    @Column(name = "changes_made")
    private String changes; // Nội dung chỉnh sửa

    @PrePersist
    public void prePersist() {
        if (editDate == null) {
            editDate = LocalDateTime.now();
        }

        if (changes == null || changes.trim().isEmpty()) {
            changes = generateAutoChangesSummary();
        }
    }

    private String generateAutoChangesSummary() {
        // Bạn có thể tùy chỉnh logic này — ví dụ:
        String productName = product != null ? product.getName() : "Unknown Product";
        String editorName = editor != null ? editor.getUsername() : "Unknown Editor";
        return "Edited by " + editorName + " on product: " + productName;
    }
}
