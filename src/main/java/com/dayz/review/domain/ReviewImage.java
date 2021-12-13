package com.dayz.review.domain;


import com.dayz.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_image")
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @Column(name = "image_file_name", nullable = false)
    private String imageFileName;

    @Column(name = "sequence")
    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public static ReviewImage of(Long id,
            String imageFileName,
            int sequence,
            Review review
    ) {
        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setId(id);
        reviewImage.setImageFileName(imageFileName);
        reviewImage.setSequence(sequence);
        reviewImage.changeReview(review);

        return reviewImage;
    }

    public static ReviewImage of(String imageFileName,
            int sequence,
            Review review
    ) {
        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setImageFileName(imageFileName);
        reviewImage.setSequence(sequence);
        reviewImage.changeReview(review);

        return reviewImage;
    }

    public void changeReview(Review review) {
        this.setReview(review);
    }

}
